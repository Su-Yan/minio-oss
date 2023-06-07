package com.geo.miniooss.service;

import com.geo.miniooss.domain.vo.ItemVo;
import io.minio.ObjectWriteResponse;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface OssTemplateService {
    /**
     * 查询指定bucket name是否存在
     * @param bucketName 桶名
     * @return
     * @throws Exception
     */
    boolean bucketIsExist(String bucketName) throws Exception;

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
    String removeBucket(String bucketName) throws Exception;

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

    /**
     * 获取文件上传外链，只用于上传
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param seconds 过期时间，单位秒钟,请注意该值必须小于7天
     * @return url
     */
    String getPutObjectURL(String bucketName, String objectName, int seconds) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     * 获取文件
     *
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
    InputStream getObject(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /** 上传文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream 文件流
     */
    ObjectWriteResponse putObject(String bucketName, String objectName, InputStream stream) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     *  上传文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream 文件流
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
    ObjectWriteResponse putObject(String bucketName, String objectName, InputStream stream, String contextType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

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
    ObjectWriteResponse putObject(String bucketName, String objectName, InputStream stream, long size,
                                  String contextType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    String putObject(String bucketName, String objectName, MultipartFile object) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;



    /**
     * 删除文件
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return
     */
    String removeObject(String bucketName, String objectName) throws Exception;

    /**
     *  检查文件是否存在
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return
     */
    boolean objectIsExists(String bucketName, String objectName) throws Exception;

    /**
     * 查询文件
     * @param bucketName bucket名称
     * @return
     */
    LinkedList<ItemVo> getAllObjectsListByBucketName(String bucketName);

    /**
     * 查询文件
     * @param bucketName bucket名称
     * @return
     */
    LinkedList<ItemVo> getAllObjectsListByRecursive(String bucketName);


}
