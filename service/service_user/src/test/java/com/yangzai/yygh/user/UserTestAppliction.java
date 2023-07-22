package com.yangzai.yygh.user;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class UserTestAppliction {
    public static void main(String[] args) {
        //数据库url、root、password信息
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/yygh_user?" +
                "serverTimezone=GMT%2B8&characterEncoding=utf-8&useSSL=false", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("yangzai") // 设置作者
//.enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\IdeaProjects\\Hospital\\yygh_parent\\service\\service_user\\src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.yangzai") // 设置父包名
                            .moduleName("yygh") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,
                                    "D:\\IdeaProjects\\Hospital\\yygh_parent\\service\\service_user\\src\\main\\resources\\mapper"));
// 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user_info") // 设置需要生成的表名
                            .addTablePrefix("tb_", "t_");// 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
