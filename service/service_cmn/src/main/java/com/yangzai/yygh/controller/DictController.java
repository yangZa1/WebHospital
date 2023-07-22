package com.yangzai.yygh.controller;


import com.yangzai.yygh.entity.Dict;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.service.IDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author yangzai
 * @since 2022-11-22
 */
@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/yygh/dict")
//@CrossOrigin
public class DictController {
    @Autowired
    IDictService dictService;

    //根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }

    //导出数据集字典接口
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse response) {
        dictService.exportDictData(response);
    }

    //导入数据字典
    @PostMapping("importData")
    public Result importDict(MultipartFile file) {
        dictService.importDictData(file);
        return Result.ok();
    }

    //根据dictcode和value查询
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value){
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }

    //根据value进行查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable("value") String value){
        String dictName = dictService.getDictName("", value);
        return dictName;
    }

    //根据dictCode获取下级节点
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public  Result findByDictCode(@PathVariable("dictCode") String dictCode){
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

}
