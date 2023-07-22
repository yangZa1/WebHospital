package com.yangzai.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.yangzai.yygh.user.mapper")
public class UserConfig {
}
