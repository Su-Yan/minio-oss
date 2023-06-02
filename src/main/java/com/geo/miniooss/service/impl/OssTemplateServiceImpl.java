package com.geo.miniooss.service.impl;

import com.geo.miniooss.constant.IMessage;
import com.geo.miniooss.service.OssTemplateService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OssTemplateServiceImpl implements OssTemplateService{
    @Autowired
    private MinioClient minioClient;

    /**
     * 查询指定bucket name是否存在
     * @param bucketName bucket名称
     * @return
     * @throws Exception
     */
    @Override
    public boolean isExist(String bucketName) throws Exception {
        boolean res = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        log.info("查询bucketName： "+bucketName+" 是否存在："+res);
        return res;
    }

    /**
     * 创建bucket
     * @param bucketName bucket名称
     * @return 000-成功，002-已存在
     * @throws Exception
     */
    @Override
    public String createBucket(String bucketName) throws Exception {
        String res = IMessage.FAILED.CODE;
        if (isExist(bucketName)){
            log.warn("该bucketName： "+bucketName+" 已存在！无法重复创建！");
            res = IMessage.EXIST.CODE;
        }else{
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("该bucketName： "+bucketName+" 创建成功！");
            res = IMessage.SUCCESS.CODE;
        }
        return res;
    }

    /**
     * 获取全部bucket
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @Override
    public List<Bucket> getAllBuckets() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Bucket> buckets = minioClient.listBuckets();
        log.info("获取到的bucket列表：");
        buckets.stream().forEach(bucket -> log.info(bucket.name()));
        return buckets;
    }

    /**
     *获取指定bucket
     * @param bucketName bucket名称
     * @return
     */
    @Override
    public Optional<Bucket> getBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Optional<Bucket> bucketOptional = minioClient.listBuckets().stream().filter(bucket -> bucket.name().equals(bucketName)).findFirst();
        if (bucketOptional.isPresent()){
            log.warn("未获取到指定bucket："+bucketName);
        }else {
            log.info("已获取bucket："+bucketName);
        }
        return bucketOptional;
    }

    /**
     * 删除指定bucket
     * @param bucketName bucket名称
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @Override
    public void removeBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        log.info("bucketName："+bucketName+" 已删除！");
    }

    /**根据文件前缀查询文件
     * @param bucketName bucket名称
     * @param prefix 前缀
     * @return
     */
    @Override
    public Iterable<Result<Item>> getAllObjectsByPrefix(String bucketName, String prefix) {
        Iterable<Result<Item>> iterable = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).build());
        log.info("根据bucketName："+bucketName+" 前缀："+prefix+" 获取到的文件信息：");
        iterable.forEach(a -> log.info(a.toString()));
        return iterable;
    }

    /**
     * 获取文件外链，只用于下载
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param seconds    过期时间，单位秒钟，请注意该值必须小于7天
     * @return url
     */
    @Override
    public String getObjectURL(String bucketName, String objectName, int seconds) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return getObjectURL(bucketName, objectName, seconds, Method.GET);
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param seconds    过期时间，单位秒钟，请注意该值必须小于7天
     * @param method     文件操作方法：GET（下载）、PUT（上传）
     * @return url
     */
    @Override
    public String getObjectURL(String bucketName, String objectName, int seconds, Method method) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(method)
                .expiry(seconds)
                .build());
        log.info("bucketName："+bucketName+" objectName："+objectName+" 该文件Url为： "+url);
        return url;
    }

    /**
     * 获取文件上传外链，只用于上传
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param seconds    过期时间，单位秒钟,请注意该值必须小于7天
     * @return url
     */
    @Override
    public String getPutObjectURL(String bucketName, String objectName, int seconds) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = getObjectURL(bucketName, objectName, seconds, Method.PUT);
        log.info("bucketName："+bucketName+" objectName: " + objectName + " 文件上传外链Url："+url);
        return url;
    }

    /**
     * 获取文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @Override
    public GetObjectResponse getObject(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectResponse getObjectResponse = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件已获取！");
        return getObjectResponse;
    }

    /** 上传文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream 文件流
     */
    @Override
    public ObjectWriteResponse putObject(String bucketName, String objectName, InputStream stream) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return putObject(bucketName, objectName, stream, stream.available(), "application/octet-stream");
    }

    /**
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param stream      文件流
     * @param contextType 类型
     * @return
     * @throws IOException
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @Override
    public ObjectWriteResponse putObject(String bucketName, String objectName, InputStream stream, String contextType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return putObject(bucketName, objectName, stream, stream.available(), contextType);
    }

    /**
     * 上传文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream 文件流
     * @param size 大小
     * @param contextType 类型
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @Override
    public ObjectWriteResponse putObject(String bucketName, String objectName, InputStream stream, long size,
                                         String contextType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件开始上传...");
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(stream, size, size)
                .contentType(contextType)
                .build());
        if (!StringUtils.isEmpty(objectWriteResponse.versionId())){
            log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件上传成功！");
        }else {
            log.error("bucketName: "+bucketName+" objectName: "+objectName+"文件上传失败！");
        }
        return objectWriteResponse;
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return
     */
    @Override
    public String removeObject(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件开始删除...");
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件删除完毕！");
        return IMessage.SUCCESS.CODE;
    }


}
