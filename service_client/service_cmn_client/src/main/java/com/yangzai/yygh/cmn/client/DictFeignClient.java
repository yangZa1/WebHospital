package com.yangzai.yygh.cmn.client;

import com.yangzai.yygh.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
@Component
public interface DictFeignClient {
    //根据dictcode和value查询
    @GetMapping("/admin/cmn/yygh/dict/getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value);

    //根据value进行查询
    @GetMapping("/admin/cmn/yygh/dict/getName/{value}")
    public String getName(@PathVariable("value") String value);


//    @GetMapping("/admin/yygh/dict/findByDictCode/{dictCode}")
//    public Result findByDictCode(@PathVariable("dictCode") String dictCode);
//
//    //根据数据id查询子数据列表
//    @GetMapping("/admin/yygh/dict/findChildData/{id}")
//    public Result findChildData(@PathVariable("id") Long id);
}
