package com.yangzai.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangzai.yygh.entity.UserInfo;
import com.yangzai.yygh.vo.user.LoginVo;
import com.yangzai.yygh.vo.user.UserAuthVo;
import com.yangzai.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author yangzai
 * @since 2022-12-01
 */
public interface IUserInfoService extends IService<UserInfo> {

    Map<String, Object> loginUser(LoginVo loginVo);

    UserInfo selectWxInfoOpenId(String openid);

    void userAuth(Long userId, UserAuthVo userAuthVo);

    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    void lock(Long userId, Integer status);

    void approval(Long userId, Integer authStatus);
}
