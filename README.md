# http_file_server

## 一.项目介绍

基于SpringBoot的HTTP文件服务器

## 二.服务端部署流程

1. 下载项目，使用maven导入IDE。

2. 创建数据库`file`,导入`attachment.sql`文件。

3. 修改`application.yml`，需要修改的有：

   1. 数据源，把账号密码改成自己的，如果数据库版本是`MySQL8`以下的需要修改`pom.xml` 和驱动名称

      ```
      spring:
          datasource:
              driver-class-name: com.mysql.jdbc.Driver
      ```

   2. 文件大小限制（默认100M）

   3. 下载目录

   4. 项目日志目录

4. 运行`FileserverApplication`中`Main`方法启动项目。

5. 默认端口 `8080`。

## 三.服务端接口介绍

### 1.上传文件

请求地址：/uploadFile

HTTP请求方式：POST

请求参数：file

### 2.下载文件

请求地址：/downloadFile

HTTP请求方式：POST

请求参数：id

### 3.获取文件信息

请求地址：/getFileInfo

HTTP请求方式：POST

请求参数：id

返回报文：示例：

```json
{
    "id": "ef57f6ca9917438c8b05632878f2abd7",
    "fileName": "80m.txt",
    "fileSize": 83886082,
    "fileType": "txt",
    "createDate": "2021-03-28",
    "fileDir": "20210328/ef57f6ca9917438c8b05632878f2abd7.txt"
}
```

