package com.yangzai.yygh.controller;

import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.service.DepartmentService;
import com.yangzai.yygh.vo.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    //根据医院编号，查询医院所有的科室列表
    @ApiOperation(value = "查询医院所有的科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable("hoscode") String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
}
