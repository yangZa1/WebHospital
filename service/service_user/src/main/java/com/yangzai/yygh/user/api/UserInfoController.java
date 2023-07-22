package com.yangzai.yygh.user.api;


import com.yangzai.yygh.entity.UserInfo;
import com.yangzai.yygh.helper.RandomUtil;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.user.service.IUserInfoService;
import com.yangzai.yygh.utils.AuthContextHolder;
import com.yangzai.yygh.vo.user.LoginVo;
import com.yangzai.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author yangzai
 * @since 2022-12-01
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    //用户认证接口
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        //传递两个参数， 第一个参数为用户id，第二个参数为认证数据vo对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request), userAuthVo);
        return Result.ok();

    }

    //获取用户id信息的接口
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return  Result.ok(userInfo);
    }

    //用户手机号登录接口
    //该方法存在bug 不能先进行手机登录再进行扫微信二维码登录
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String, Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }

    //发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone) {
        //从redis获取验证码，如果获取获取到，返回ok
        // key 手机号  value 验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        //如果从redis获取不到，
        // 生成验证码，
        code = RandomUtil.getSixBitRandom();
        System.out.println("手机号" + phone + "发送的验证码为 : " + code);
        //调用service方法，通过整合短信服务进行发送
        //boolean isSend = msmService.send(phone,code);
        //生成验证码放到redis里面，设置2分钟有效时间
        //if(isSend) {
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            return Result.ok();
        //} else {
            //return Result.fail().message("发送短信失败");
        //}
    }
}
