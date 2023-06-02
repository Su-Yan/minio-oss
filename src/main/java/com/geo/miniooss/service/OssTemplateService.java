package com.geo.miniooss.service;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface OssTemplateService {
    /**
     * 查询指定bucket name是否存在
     * @param bucketName 桶名
     * @return
     * @throws Exception
     */
    boolean isExist(String bucketName) throws Exception;

    /**
     * 创建bucket
     * @param bucketName bucket名称
     * @return 000-成功，002-已存在
     * @throws Exception
     */
    String createBucket(String bucketName) throws Exception;

    /**
     * 获取全部bucket
     * @return
     */
    List<Bucket> getAllBuckets() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     *获取指定bucket
     * @param bucketName bucket名称
     * @return
     */
    Optional<Bucket> getBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

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
    void removeBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**根据文件前缀查询文件
     * @param bucketName bucket名称
     * @param prefix 前缀
     * @return
     */
    Iterable<Result<Item>> getAllObjectsByPrefix(String bucketName, String prefix);

    /**
     * 获取文件外链，只用于下载
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param seconds 过期时间，单位秒钟，请注意该值必须小于7天
     * @return url
     */
    String getObjectURL(String bucketName, String objectName, int seconds) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     *  获取文件外链
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param seconds 过期时间，单位秒钟，请注意该值必须小于7天
     * @param method 文件操作方法：GET（下载）、PUT（上传）
     * @return url
     */
    String getObjectURL(String bucketName, String objectName, int seconds, Method method) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
