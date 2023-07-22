package com.yangzai.yygh.repository;

import com.yangzai.yygh.entity.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
