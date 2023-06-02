package com.geo.miniooss.controller;

import com.geo.miniooss.service.OssTemplateService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/oss")
public class OssEndpointController {

    @Autowired
    private OssTemplateService ossTemplateService;

    @GetMapping("/bucket/isexist")
    public boolean isExist(@RequestParam("bucketName")String bucketName) throws Exception {
        return ossTemplateService.isExist(bucketName);
    }

    @GetMapping("/bucket/getfileurl")
    public String getFileUrl(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ossTemplateService.getObjectURL(bucketName,objectName,3600);
    }
}
