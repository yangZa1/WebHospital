package com.yangzai.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangzai.yygh.entity.UserInfo;
import com.yangzai.yygh.exception.YyghException;
import com.yangzai.yygh.helper.JwtHelper;
import com.yangzai.yygh.result.AuthStatusEnum;
import com.yangzai.yygh.result.ResultCodeEnum;
import com.yangzai.yygh.user.mapper.UserInfoMapper;
import com.yangzai.yygh.user.service.IUserInfoService;
import com.yangzai.yygh.vo.user.LoginVo;
import com.yangzai.yygh.vo.user.UserAuthVo;
import com.yangzai.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author yangzai
 * @since 2022-12-01
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //从loginVo中获取输入的手机号 和 验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //判断手机号和验证码是否为空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code))
            throw  new YyghException(ResultCodeEnum.PARAM_ERROR);

        //判断手机验证码和输入的验证码是否一致

        //校验校验验证码
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }


        //绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }


        //判断是否是第一次登录，根据手机号查询数据库：
        //1、不存在相同的手机号 就是第一次登陆 帮他注册
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone", phone);
        userInfo = baseMapper.selectOne(queryWrapper);
        if(userInfo == null){
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            this.save(userInfo);
        }
        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        //2、存在相同的手机号 直接登录
        //返回登录信息 用户名、token信息
        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        //token生成
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("name", name);
        map.put("token", token);

        return map;
    }

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("openid", openid);
        UserInfo userInfo = baseMapper.selectOne(objectQueryWrapper);
        return userInfo;
    }

    //用户认证
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询出用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息 (认证人姓名和其他信息)
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        //进行信息更新
        baseMapper.updateById(userInfo);
    }

    //用户列表（条件查询带分页）
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //userInfoQueryVo中获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus(); //用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();//开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();//结束时间
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) wrapper.like("name", name);
        if(!StringUtils.isEmpty(status)) wrapper.eq("status", status);
        if(!StringUtils.isEmpty(authStatus)) wrapper.eq("auth_status", authStatus);
        if(!StringUtils.isEmpty(createTimeBegin)) wrapper.ge("creat_time", createTimeBegin);
        if(!StringUtils.isEmpty(createTimeEnd)) wrapper.le("creat_time", createTimeEnd);

        //调用mapper的方法
        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应的值
        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });
        return pages;
    }

    //用户锁定状态修改
    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = this.getById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }

    }

    //认真审批 认证成功：authStatus状态值变为2或-1 （1为认证中，2为认证通过，-1为认证不通过）
    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue()==2 || authStatus.intValue()==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }

    }

    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证状态的编码
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0 1
        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString", statusString);
        return userInfo;
    }
}
