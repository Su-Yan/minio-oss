package com.geo.miniooss.controller;

import com.geo.miniooss.domain.vo.ItemVo;
import com.geo.miniooss.service.OssTemplateService;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/object/getFileUrl")
    public String getFileUrl(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ossTemplateService.getObjectURL(bucketName,objectName,3600);
    }

    @GetMapping("/object/isExists")
    public boolean objectIsExists(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws Exception {
        return ossTemplateService.objectIsExists(bucketName, objectName);
    }

    @GetMapping("/object/list")
    public LinkedList<ItemVo> getAllObjectList(@RequestParam("bucketName")String bucketName){
        LinkedList<ItemVo> items = ossTemplateService.getAllObjectsListByRecursive(bucketName);
        return items;
    }

    @PostMapping("/object/uploadFile")
    public String uploadFile(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName, @RequestParam("file") MultipartFile object) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String res = ossTemplateService.putObject(bucketName, objectName, object);
        return res;
    }

    @GetMapping("/object/getUploadUrl")
    public String getUploadUrl(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String res = ossTemplateService.getPutObjectURL(bucketName,objectName,10*60);
        return res;
    }

    @PostMapping("/object/uploadFileByFragmentation")
    public String uploadFileByFragmentation(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName, @RequestParam("file") MultipartFile object){
        String res = null;
        return res;
    }

    @DeleteMapping("/object/deleteObject")
    public String deleteObject(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws Exception {
        String res = ossTemplateService.removeObject(bucketName, objectName);
        return res;
    }
}
