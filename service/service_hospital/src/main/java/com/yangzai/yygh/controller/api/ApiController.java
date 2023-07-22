package com.yangzai.yygh.controller.api;

import com.yangzai.yygh.entity.Department;
import com.yangzai.yygh.entity.Hospital;
import com.yangzai.yygh.entity.Schedule;
import com.yangzai.yygh.exception.YyghException;
import com.yangzai.yygh.helper.HttpRequestHelper;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.result.ResultCodeEnum;
import com.yangzai.yygh.service.DepartmentService;
import com.yangzai.yygh.service.HospitalService;
import com.yangzai.yygh.service.IHospitalSetService;
import com.yangzai.yygh.service.ScheduleService;
import com.yangzai.yygh.utils.MD5;
import com.yangzai.yygh.vo.DepartmentQueryVo;
import com.yangzai.yygh.vo.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private IHospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //签名验证公共方法
    public boolean signCheck(Map<String, Object> paramMap){

        //获取医院编号：
        String hoscode = (String) paramMap.get("hoscode");
        //1、获取医院系统传递过来的签名
        String hospSign = (String) paramMap.get("sign");
        //2、根据传递过来的医院编码，查询数据库 查询对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3、把数据库传过来的签名进行MD5加密
        String signKeyMD5 = MD5.encrypt(signKey);
        return hospSign.equals(signKeyMD5);
    }

    //上传排班接口
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        //获取到传递过来的排班的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //签名校验

        //调service方法保存
        scheduleService.save(paramMap);
        return  Result.ok();

    }

    //查询排班接口
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        //获取到传递过来的科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号、科室编号、当前页、每页记录数
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");

        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 3 : Integer.parseInt((String) paramMap.get("limit"));

        //签名校验
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        Page<Schedule> pageModel = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }

    //删除排班接口
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        //获取到传递过来的科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //医院编号 排班编号：
        String hoscode = (String) paramMap.get("hoscode");
//        String depcode = (String) paramMap.get("depcode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        //签名校验
        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }

    //删除科室接口
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取到传递过来的科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //医院编号 科室编号：
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        //签名校验
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取到传递过来的科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号、当前页、每页记录数
        String hoscode = (String) paramMap.get("hoscode");

        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 3 : Integer.parseInt((String) paramMap.get("limit"));

        //签名校验
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    //上传科室接口
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取到传递过来的医院的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        if(!this.signCheck(paramMap)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service方法
        departmentService.save(paramMap);
        return Result.ok();
    }
    //查询医院接口
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取到传递过来的医院的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号：
        String hoscode = (String) paramMap.get("hoscode");
        //1、获取医院系统传递过来的签名
        String hospSign = (String) paramMap.get("sign");
        //2、根据传递过来的医院编码，查询数据库 查询对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3、把数据库传过来的签名进行MD5加密
        String signKeyMD5 = MD5.encrypt(signKey);
        //4、判断加密后的signkey是否相同
        if(!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //调用service方法实现根据医院编号查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request){
        //获取到传递过来的医院的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //1、获取医院系统传递过来的签名
        String hospSign = (String) paramMap.get("sign");
        //2、根据传递过来的医院编码，查询数据库 查询对应的医院签名
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        System.out.println(hospSign);
        //3、把数据库传过来的签名进行MD5加密
        String signKeyMD5 = MD5.encrypt(signKey);
        System.out.println(signKeyMD5);
        //4、判断加密后的signkey是否相同
        if(!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String)paramMap.get("logoData");
        if(!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
        }
        //调用service方法保存医院信息
        hospitalService.save(paramMap);
        return Result.ok();
    }


}
