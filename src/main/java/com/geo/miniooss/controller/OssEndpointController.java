package com.geo.miniooss.controller;

import com.geo.miniooss.domain.vo.ItemVo;
import com.geo.miniooss.service.OssTemplateService;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

@Slf4j
@RestController
@RequestMapping("/oss")
public class OssEndpointController {

    @Autowired
    private OssTemplateService ossTemplateService;

    @PostMapping("/bucket/createBucket")
    public String createBucket(@RequestParam("bucketName")String bucketName) throws Exception {
        return ossTemplateService.createBucket(bucketName);
    }

    @GetMapping("/bucket/isExists")
    public boolean isExists(@RequestParam("bucketName")String bucketName) throws Exception {
        return ossTemplateService.bucketIsExist(bucketName);
    }

    @DeleteMapping("/bucket/deleteBucket")
    public String deleteBucket(@RequestParam("bucketName")String bucketName) throws Exception {
        return ossTemplateService.removeBucket(bucketName);
    }

    @GetMapping("/bucket/getFileUrl")
    public String getFileUrl(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ossTemplateService.getObjectURL(bucketName,objectName,3600);
    }

    @GetMapping("/object/isExists")
    public boolean objectIsExists(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ossTemplateService.objectIsExists(bucketName, objectName);
    }

    @GetMapping("/object/list")
    public LinkedList<ItemVo> getAllObjectList(@RequestParam("bucketName")String bucketName){
        LinkedList<ItemVo> items = ossTemplateService.getAllObjectsListByBucketName(bucketName);
        items.forEach(item -> log.info(item.objectName));
        return items;
    }
}
