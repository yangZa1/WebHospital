package com.yangzai.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yangzai.yygh.entity.UserInfo;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.user.service.IUserInfoService;
import com.yangzai.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制层
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    IUserInfoService userInfoService;

    //用户列表（条件查询带分页）
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit,
                       UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageParam, userInfoQueryVo);

        return Result.ok(pageModel);
    }

    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public Result lock(
            @PathVariable("userId") Long userId,
            @PathVariable("status") Integer status){
        userInfoService.lock(userId, status);
        return Result.ok();
    }

    //认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        userInfoService.approval(userId,authStatus);
        return Result.ok();
    }

}
