# minio-oss
### yansugeo

---
## 简介
**MinIO** 是一款**高性能、分布式**的对象存储系统. 它是一款软件产品, 可以100%的运行在标准硬件。即X86等低成本机器也能够很好的运行MinIO。
MinIO提供高性能、S3兼容的对象存储。Minio 是一个基于Go语言的对象存储服务。它实现了大部分亚马逊S3云存储服务接口，可以看做是是S3的开源版本，非常适合于存储大容量非结构化的数据，例如图片、视频、日志文件、备份数据和容器/虚拟机镜像等，而一个对象文件可以是任意大小，从几kb到最大5T不等。区别于分布式存储系统，minio的特色在于简单、轻量级，对开发者友好，认为存储应该是一个开发问题而不是一个运维问题。

---
## 使用

### 基础概念
1. **Object**：存储到 Minio 的基本对象，如***文件***、***字节流***、***视频***、***音频***、***日志***、***镜像***等等

2. **Bucket**：用来***存储 Object*** 的逻辑空间。每个 Bucket 之间的数据是***相互隔离***的。对于客户端而言，就相当于一个存放文件的***顶层***文件夹。

3. **Drive**：即存储数据的磁盘，在 MinIO 启动时，以参数的方式传入。Minio 中所有的对象数据都会 存储在 Drive 里。

4. **Set** ：即一组 Drive 的集合，分布式部署根据集群规模自动划分一个或多个 Set ，每个 Set 中的 Drive 分布在不同位置。一个对象存储在一个 Set 上。（For example: {1…64} is divided into 4 sets each of size 16.）

### MinIO bucket命名规则
1. Bucket名称在Amazon S3中的所有现有Bucket名称中必须是***唯一***的；
2. Bucket名称必须符合DNS命名约定；
3. 存储桶名称的长度必须***至少***为***3个字符***，且***不得***超过***63个字符***；
4. Bucket名称***不能***包含***大写字符***或***下划线***；
5. Bucket名称***必须***以***小写字母***或***数字开头***；
6. Bucket名称必须是一系列一个或多个标签。相邻的标签用一个句点（.）分隔。Bucket名称可以包含小写字母、数字和连字符。每个标签必须以小写字母或数字开头和结尾；
7. Bucket名称***不能***格式化为***IP地址***（例如192.168.5.4）；
8. 当使用带有安全套接字层（SSL）的虚拟托管样式存储桶时，SSL通配符证书只匹配不包含句点的存储桶。要解决此问题，请使用HTTP或编写自己的证书验证逻辑。建议在使用虚拟托管样式存储桶时，***不要***在存储桶名称中使用***句点（“.”）***。

### 代码

1. 代码地址：[Github](https://github.com/Su-Yan/minio-oss.git)
2. 目录结构：
- miniooss
  - config
    - MinioConfig
      > 用于初始化minioClient的文件
    - OssProperties
      > 用于初始化minioClient的配置参数，参数从Nacos的ksrcb-oss-application.yml获取，主要为：
      ```yaml
      minio:
        endpoint: http://ip:port  #ip、端口、账号、密码请自行替换
        access-key: account
        secret-key: password
      ```
  - constant
    - IConstant
      > 代码中用到的常量在此定义
  - controller
    - OssEndpointController
      > 后端Controller
  - domain
    - vo
      - ItemVo
        > 用于存储minio中object的元数据
  - service
    - impl
      - OssTemplateServiceImpl
        > 功能实现
    - OssTemplateService
- resources
  - bootstrap.yml
    > 配置文件
3. 如有文件相关操作，请在自己的微服务中使用feign远程调用OssEndpointController；
4. OssEndpointController方法详解，***使用时bucketName仅传输桶名（如dops），objectName请带上相应路径，如(test/test2/test.png)：***
   1. 创建bucket
      ```java
      @PostMapping("/bucket/createBucket")
      public String createBucket(@RequestParam("bucketName")String bucketName) throws Exception {
        return ossTemplateService.createBucket(bucketName);
      }
      ```
   2. 检查bucket是否存在
      ```java
      @GetMapping("/bucket/isExists")
      public boolean isExists(@RequestParam("bucketName")String bucketName) throws Exception {
        return ossTemplateService.bucketIsExist(bucketName);
      }
      ```
   3. 删除bucket ***删除会导致该bucket下所有文件删除！慎用！！！***
      ```java
      @DeleteMapping("/bucket/deleteBucket")
      public String deleteBucket(@RequestParam("bucketName")String bucketName) throws Exception {
        return ossTemplateService.removeBucket(bucketName);
      }
      ```
   4. 获取object下载外链 ***可用于前端\<a>标签展示，从此方法获取的链接，点击即可下载文件***
      ```java
      @GetMapping("/object/getFileUrl")
      public String getFileUrl(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ossTemplateService.getObjectURL(bucketName,objectName,3600);
      }
      ```
   5. 判断object是否存在
      ```java
      @GetMapping("/object/isExists")
      public boolean objectIsExists(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws Exception {
        return ossTemplateService.objectIsExists(bucketName, objectName);
      }
      ```
   6. 获取object列表
      ```java
      @GetMapping("/object/list")
      public LinkedList<ItemVo> getAllObjectList(@RequestParam("bucketName")String bucketName){
        LinkedList<ItemVo> items = ossTemplateService.getAllObjectsListByRecursive(bucketName);
        return items;
      }
      ```
   7. 上传object
      ```java
      @PostMapping("/object/uploadFile")
      public String uploadFile(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName, @RequestParam("file") MultipartFile object) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String res = ossTemplateService.putObject(bucketName, objectName, object);
        return res;
      }
      ```
   8. 删除object
      ```java
      @DeleteMapping("/object/deleteObject")
      public String deleteObject(@RequestParam("bucketName")String bucketName, @RequestParam("objectName")String objectName) throws Exception {
        String res = ossTemplateService.removeObject(bucketName, objectName);
      return res;
      }
      ```
    



