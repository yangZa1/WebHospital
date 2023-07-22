package com.yangzai.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangzai.yygh.entity.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {
    List<Patient> findAllUserId(Long userId);

    Patient getPatientId(Long id);
}
