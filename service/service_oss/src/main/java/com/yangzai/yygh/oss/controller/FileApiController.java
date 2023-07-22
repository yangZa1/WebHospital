package com.yangzai.yygh.oss.controller;

import com.yangzai.yygh.oss.service.FileService;
import com.yangzai.yygh.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {

    @Autowired
    private FileService fileService;

    //上传文件到阿里云oss
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        //获取前端上传的文件对象,返回文件存放的路径
        String fileUrl = fileService.upload(file);
        return Result.ok(fileUrl);
    }
}
