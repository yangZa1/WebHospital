package com.yangzai.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangzai.yygh.cmn.client.DictFeignClient;
import com.yangzai.yygh.entity.Patient;
import com.yangzai.yygh.result.DictEnum;
import com.yangzai.yygh.user.mapper.PatientMapper;
import com.yangzai.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImp extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;
    //查询就诊人信息
    @Override
    public List<Patient> findAllUserId(Long userId) {
        //根据userId查询所有就诊人信息的列表
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id" , userId);
        List<Patient> patients = baseMapper.selectList(wrapper);
        //通过远程调用，得到编码对应的具体内容（查询数据字段表）
        patients.stream().forEach(item -> {
            //其他参数的封装
            this.packPatient(item);
        });
        return patients;
    }

    @Override
    public Patient getPatientId(Long id) {
        Patient patient = baseMapper.selectById(id);
        this.packPatient(patient);
        return patient;
    }

    //Patient对象中其他参数的封装
    private Patient packPatient(Patient patient) {

        String certificatesTypeString = dictFeignClient.
                getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());
        //联系人证件类型
        String contactsCertificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;

    }
}
