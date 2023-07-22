package com.yangzai.yygh.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.yangzai.yygh.order.mapper")
public class OrderConfig {
}
