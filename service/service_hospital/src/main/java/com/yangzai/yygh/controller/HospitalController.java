package com.yangzai.yygh.controller;

import com.yangzai.yygh.cmn.client.DictFeignClient;
import com.yangzai.yygh.entity.Dict;
import com.yangzai.yygh.entity.Hospital;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.service.HospitalService;
import com.yangzai.yygh.vo.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
//跨域问题解决注解
//@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    //医院列表（条件查询分页）
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable("page") Integer page,
                           @PathVariable("limit") Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);

        return Result.ok(pageModel);
    }

    //更新医院上线状体
    @ApiOperation(value = "更新医院上线状体")
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable("id") String id,
                                    @PathVariable("status") Integer status){
        hospitalService.updateStatus(id, status);
        return Result.ok();

    }

    //医院详情信息
    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable("id") String id){
        Map<String, Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }

    @Autowired
    private DictFeignClient dictFeignClient;

//    //远程调用dict中的方法
//    @GetMapping("findByDictCode/{dictCode}")
//    public  Result findByDictCode(@PathVariable("dictCode") String dictCode){
//        return dictFeignClient.findByDictCode(dictCode);
//    }
//
//    //远程调用dict中的方法
//    @GetMapping("findChildData/{id}")
//    public  Result findChildData(@PathVariable("id") Long id){
//        return dictFeignClient.findChildData(id);
//    }
}
