package com.yangzai.yygh.service;

import com.yangzai.yygh.entity.Department;
import com.yangzai.yygh.entity.Schedule;
import com.yangzai.yygh.vo.DepartmentQueryVo;
import com.yangzai.yygh.vo.DepartmentVo;
import com.yangzai.yygh.vo.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode, String depcode);

    //根据医院编号，查询医院所有的科室列表
    List<DepartmentVo> findDeptTree(String hoscode);

    String getDepName(String hoscode,String depcode);

    Department getDepartment(String hoscode, String depcode);
}
