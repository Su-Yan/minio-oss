package com.geo.miniooss.service.impl;

import com.geo.miniooss.constant.IConstant;
import com.geo.miniooss.domain.vo.ItemVo;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
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
    public boolean bucketIsExist(String bucketName) throws Exception {
        boolean res = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        log.info("查询bucketName： "+bucketName+" 是否存在："+res);
        return res;
    }

    public boolean isFile(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
//        log.info("bucketName: "+bucketName+"，objectName: "+objectName+" 是否为文件："+ !statObjectResponse);
        return false;
    }

    /**
     * 创建bucket
     * @param bucketName bucket名称
     * @return 000-成功，002-已存在
     * @throws Exception
     */
    @Override
    public String createBucket(String bucketName) throws Exception {
        String res = IConstant.FAILED.CODE;
        if (bucketIsExist(bucketName)){
            log.warn("该bucketName： "+bucketName+" 已存在！无法重复创建！");
            res = IConstant.EXIST.CODE;
        }else{
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("该bucketName： "+bucketName+" 创建成功！");
            res = IConstant.SUCCESS.CODE;
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
    public String removeBucket(String bucketName) throws Exception {
        String res;
        if (bucketIsExist(bucketName)){
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            res = IConstant.SUCCESS.CODE;
            log.info("bucketName："+bucketName+" 已删除！");
        }else {
            log.info("bucketName："+bucketName+" 不存在，无需删除！");
            res = IConstant.NOTEXIST.CODE;
        }
        return res;
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

    public void getAllObjectsByPrefix(String bucketName, String prefix, LinkedList<Item> objList) {
        Iterable<Result<Item>> iterable = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).build());
        iterable.forEach(a -> {
            try {
                objList.add(a.get());
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 查询文件
     *
     * @param bucketName bucket名称
     * @return
     */
    @Override
    public LinkedList<ItemVo> getAllObjectsListByBucketName(String bucketName) {
        Iterable<Result<Item>> iterable = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        LinkedList<Item> objList = new LinkedList<>();
        LinkedList<ItemVo> resList = new LinkedList<>();
        log.info("bucketName: "+bucketName+"下所有的文件：");
        iterable.forEach(itemResult -> {
            try {
                objList.add(itemResult.get());
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            }
        });
        while(!objList.isEmpty()){
            Item tmp = objList.removeFirst();
            if (tmp.isDir()){
                getAllObjectsByPrefix(bucketName,tmp.objectName(),objList);
            }else {
                resList.add(new ItemVo(tmp));
            }
        }
        resList.forEach(item -> log.info(item.objectName));
        return resList;
    }

    /**
     * 查询文件
     * @param bucketName bucket名称
     * @return
     */
    @Override
    public LinkedList<ItemVo> getAllObjectsListByRecursive(String bucketName) {
        Iterable<Result<Item>> iterable = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());
        LinkedList<ItemVo> itemVos = new LinkedList<>();
        iterable.forEach(item -> {
            try {
                itemVos.add(new ItemVo(item.get()));
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("bucketName: "+bucketName+"下所有的文件：");
        itemVos.forEach(itemVo -> log.info(itemVo.objectName));
        return itemVos;
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
        String url = URLDecoder.decode(minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(method)
                .expiry(seconds)
                .build()), IConstant.UTF8.CODE);

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
    @Override
    public InputStream getObject(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件已获取！");
        return inputStream;
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
                .stream(stream, size, -1)
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
     * @param bucketName
     * @param objectName
     * @param object
     */
    @Override
    public String putObject(String bucketName, String objectName, MultipartFile object) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String res = IConstant.FAILED.CODE;
        InputStream inputStream = object.getInputStream();
        String name = object.getOriginalFilename();
        ObjectWriteResponse objectWriteResponse = putObject(bucketName, objectName, inputStream, object.getSize(), object.getContentType());
        if (objectWriteResponse.object().equals(objectName)){
            res = IConstant.SUCCESS.CODE;
        }
        return res;
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return
     */
    @Override
    public String removeObject(String bucketName, String objectName) throws Exception {
        String res =IConstant.FAILED.CODE;
        if (objectIsExists(bucketName, objectName)){
            log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件开始删除...");
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件删除完毕！");
        }else {
            log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件不存在，无需删除！");
            res = IConstant.NOTEXIST.CODE;
        }
        return res;
    }

    /**
     * 检查文件是否存在
     *
     * @param bucketName  bucket名称
     * @param objectName 文件名称
     * @return
     */
    @Override
    public boolean objectIsExists(String bucketName, String objectName) throws Exception {
        boolean res = false;
        if (!bucketIsExist(bucketName)){
            res = false;
            return res;
        }
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            long size = stat.size();
            if (size!=0){
                log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件存在！size: "+size);
                res = true;
            }
        }catch (Exception exception){
            log.warn(exception.getMessage());
            if (exception.getMessage().contains("Object does not exist")){
                log.info("bucketName: "+bucketName+" objectName: "+objectName+"文件不存在！");
                res = false;
            }
        }
        return res;
    }


}
