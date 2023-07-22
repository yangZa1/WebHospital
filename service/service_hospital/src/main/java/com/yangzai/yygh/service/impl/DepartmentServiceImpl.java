package com.yangzai.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yangzai.yygh.entity.Department;
import com.yangzai.yygh.repository.DepartmentRepository;
import com.yangzai.yygh.service.DepartmentService;
import com.yangzai.yygh.vo.DepartmentQueryVo;
import com.yangzai.yygh.vo.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService
{
    @Autowired
    private DepartmentRepository departmentRepository;

    //上传科室接口
    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap转换为department对象
        String jsonString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(jsonString, Department.class);
        Department departmentExist = departmentRepository
                .getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        //判断
        if(departmentExist != null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {

        //创建department对象
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        //创建Pageable对象 设置当前页以及每页记录数 0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                .withIgnoreCase(true);
        Example<Department> example = Example.of(department, matcher);
        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    //删除科室接口
    @Override
    public void remove(String hoscode, String depcode) {
        //根据医院编号和科室编号查询
        Department deparment = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(deparment != null){
            //调用方法删除
            departmentRepository.deleteById(deparment.getId());
        }
    }

    //根据医院编号，查询医院所有的科室列表
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，用于最终数据的封装
        ArrayList<DepartmentVo> result = new ArrayList<>();

        //根据医院的编号，查询医院所有的科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
        //所有科室列表的信息
        List<Department> departmentList = departmentRepository.findAll(example);
        //把所有科室按照树形结构显示
        //根据大科室编号 bigcode 进行分组， 获得每个大科室里面下级子科室（该方法得到以大科室编号为key，所有子科室list为value的map数组）
        Map<String, List<Department>> departmentMap = departmentList.stream()
                                            .collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合
        for(Map.Entry<String, List<Department>> entry : departmentMap.entrySet()){
            //大科室的编号
            String bigcode = entry.getKey();

            //大科室编号对应的全部小科室
            List<Department> departments = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigcode);
            departmentVo.setDepname(departments.get(0).getBigname());
            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for(Department dept : departments){
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode(dept.getDepcode());
                departmentVo1.setDepname(dept.getDepname());
                children.add(departmentVo1);
            }
            departmentVo.setChildren(children);
            //放到最终的result里面
            result.add(departmentVo);
        }

        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null)
        return department.getDepname();
        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }

}
