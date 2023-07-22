package com.yangzai.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yangzai.yygh.cmn.client.DictFeignClient;
import com.yangzai.yygh.entity.Hospital;
import com.yangzai.yygh.repository.HospitalRepository;
import com.yangzai.yygh.service.HospitalService;
import com.yangzai.yygh.vo.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServcieImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        //把参数map集合转换为hospital对象 1、map集合转化为字符串 2、字符串转化为对象
        String jsonString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);

        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        if(hospitalExist != null){
            //如果存在，进行修改操作
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);

        }else {
            //如果不在，进行添加操作
            //0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);

        }

    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }


    @Autowired
    private DictFeignClient dictFeignClient;

    //医院列表（条件查询带分页）
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);

        //创建条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                        .withIgnoreCase(true);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建example示例
        Example<Hospital> example = Example.of(hospital, exampleMatcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        //获取查询list集合，遍历进行医院等级封装
        pages.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });


        return pages;
    }

    //更新医院上线状体
    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object> getHospById(String id) {
        //包含醫院等級的醫院信息
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        HashMap<String, Object> result = new HashMap<>();
        result.put("hospital", hospital);
        //单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);

        return result;
    }

    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital != null){
            return hospital.getHosname();
        }
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    //根据医院编号获取医院详情以及预约规则
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String,Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));

        result.put("hospital",hospital);
        result.put("bookingRule", hospital.getBookingRule());
        hospital.setBookingRule(null);
        return result;
    }

    private Hospital setHospitalHosType(Hospital item) {
        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", item.getHostype());
        //查询省市区
        String provinceStrig = dictFeignClient.getName(item.getProvinceCode());
        String cityString = dictFeignClient.getName(item.getCityCode());
        String districtString = dictFeignClient.getName(item.getDistrictCode());
        item.getParam().put("hostypeString", hostypeString);
        item.getParam().put("fullAddress", provinceStrig + cityString + districtString);
        return item;
    }
}
