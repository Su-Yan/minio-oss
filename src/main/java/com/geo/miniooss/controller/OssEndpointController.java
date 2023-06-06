package com.geo.miniooss.controller;

import com.geo.miniooss.service.OssTemplateService;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

@RestController
@RequestMapping("/oss")
public class OssEndpointController {

    @Autowired
    private OssTemplateService ossTemplateService;

    @PostMapping("/bucket/{bucketName}")
    public Bucket createBucket(@PathVariable @NotBlank String bucketName) throws Exception {
        ossTemplateService.createBucket(bucketName);
        return ossTemplateService.getBucket(bucketName).get();
    }

    @GetMapping("/bucket/isexist")
    public boolean isExists(@RequestParam("bucketName")String bucketName) throws Exception {
        return ossTemplateService.isExist(bucketName);
    }

    @GetMapping("/bucket/getfileurl")
    public String getFileUrl(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ossTemplateService.getObjectURL(bucketName,objectName,3600);
    }

    @GetMapping("/object/isexist")
    public boolean objectIsExists(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ossTemplateService.objectIsExists(bucketName, objectName);
    }

    @GetMapping("/object/list")
    public LinkedList<String> getAllObjectList(@RequestParam("bucketName")String bucketName){
        return ossTemplateService.getAllObjectsListByBucketName(bucketName);
    }
}
