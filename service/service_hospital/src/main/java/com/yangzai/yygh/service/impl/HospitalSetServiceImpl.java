package com.yangzai.yygh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangzai.yygh.entity.HospitalSet;
import com.yangzai.yygh.exception.YyghException;
import com.yangzai.yygh.mapper.HospitalSetMapper;
import com.yangzai.yygh.result.ResultCodeEnum;
import com.yangzai.yygh.service.IHospitalSetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangzai.yygh.vo.order.SignInfoVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 医院设置表 服务实现类
 * </p>
 *
 * @author yangzai
 * @since 2022-11-18
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements IHospitalSetService {

    @Override
    public int DeleteHospSetById(Long id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hoscode);
        return baseMapper.selectOne(queryWrapper).getSignKey();
    }

    //获取签名信息
    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if(null == hospitalSet) {
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;

    }
}
