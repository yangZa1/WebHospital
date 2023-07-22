package com.yangzai.yygh.service;


import com.yangzai.yygh.entity.Hospital;
import com.yangzai.yygh.vo.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    //演员列表（条件查询带分页）
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Map<String, Object> getHospById(String id);

    String getHospName(String hoscode);

    //根据医院名称查询
    List<Hospital> findByHosname(String hosname);

    Map<String, Object> item(String hoscode);
}
