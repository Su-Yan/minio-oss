package com.geo.miniooss.service.impl;

import com.geo.miniooss.constant.IMessage;
import com.geo.miniooss.service.OssTemplateService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

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
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
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
            res = IMessage.EXIST.CODE;
        }else{
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
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
        return minioClient.listBuckets();
    }

    /**
     *获取指定bucket
     * @param bucketName bucket名称
     * @return
     */
    @Override
    public Optional<Bucket> getBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.listBuckets().stream().filter(bucket -> bucket.name().equals(bucketName)).findFirst();
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
    }

    /**根据文件前缀查询文件
     * @param bucketName bucket名称
     * @param prefix 前缀
     * @return
     */
    @Override
    public Iterable<Result<Item>> getAllObjectsByPrefix(String bucketName, String prefix) {
        return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).build());
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
        return url;
    }

}
