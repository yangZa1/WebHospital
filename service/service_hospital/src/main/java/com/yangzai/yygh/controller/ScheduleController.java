package com.yangzai.yygh.controller;

import com.yangzai.yygh.entity.Schedule;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.service.ScheduleService;
import com.yangzai.yygh.vo.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

//@CrossOrigin
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;

    //根据医院的编号和科室编号，查询排班的规则数据
    @ApiOperation(value = "查询排班的规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getDeptList(@PathVariable("page") long page,
                              @PathVariable("limit") long limit,
                              @PathVariable("hoscode") String hoscode,
                              @PathVariable("depcode") String depcode ){
        Map<String,Object> map = scheduleService.getScheduleRule(page,limit,hoscode,depcode);
        return Result.ok(map);
    }

    //根据医院编号、科室编号、工作日期，查询排班的详细信息
    @ApiOperation(value = "查询排班的详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable("hoscode") String hoscode,
                                    @PathVariable("depcode") String depcode ,
                                    @PathVariable("workDate") String workDate) {
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(list);
    }

}
