package com.yangzai.yygh.user.api;

import com.yangzai.yygh.entity.Patient;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.user.service.PatientService;
import com.yangzai.yygh.utils.AuthContextHolder;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Autowired
    private PatientService patientService;

    //获取就诊人列表
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.findAllUserId(userId);
        return Result.ok(list);
    }
    //添加就诊人
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody Patient patient , HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    //根据就诊人id获取就诊人的信息
    @GetMapping("auth/get/{id}")
    public Result getPatient(@PathVariable("id") Long id){
        Patient patient = patientService.getPatientId(id);
        return Result.ok(patient);
    }

    //修改就诊人
    @PostMapping("auth/update")
    public Result updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.ok();
    }
    //删除就诊人
    @DeleteMapping("auth/remove/{id}")
    public Result removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "获取就诊人")
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(
            @ApiParam(name = "id", value = "就诊人id", required = true)
            @PathVariable("id") Long id) {
        //查询出就诊人并且向对象里面保存信息
        return patientService.getPatientId(id);
    }

}
