package com.yangzai.yygh.service;

import com.yangzai.yygh.entity.HospitalSet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangzai.yygh.vo.order.SignInfoVo;

/**
 * <p>
 * 医院设置表 服务类
 * </p>
 *
 * @author yangzai
 * @since 2022-11-18
 */
public interface IHospitalSetService extends IService<HospitalSet> {
    int DeleteHospSetById(Long id);

    String getSignKey(String hoscode);

    SignInfoVo getSignInfoVo(String hoscode);
}
