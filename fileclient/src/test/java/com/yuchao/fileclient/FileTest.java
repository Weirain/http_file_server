package com.yuchao.fileclient;

import com.alibaba.fastjson.JSONObject;
import com.yuchao.fileclient.client.FileClient;
import com.yuchao.fileclient.model.ResponseVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

/**
 * 事先准备好测试目录、测试文件、数据库数据
 * @author yuchao
 * @date 2021/3/27 11:14 上午
 **/
public class FileTest {

    // 测试上传文件目录
    private static final String FILE_PATH = "/Users/yuchao/file/testfile/";
    // 保存的文件的目录，最后不要带分隔符。
    private static final String SAVE_PATH = "/Users/yuchao/file/tmp";
    // <100M的文件 mkfile命令创建
    private static final String FILE_IN_SIZE = FILE_PATH + "80m.txt";
    // =100M的文件 mkfile命令创建
    private static final String FILE_ON_SIZE = FILE_PATH + "100m.txt";
    // >100M的文件 mkfile命令创建
    private static final String FILE_OUT_SIZE = FILE_PATH + "100.01m.txt";

    private static final int HTTP_OK = 200;
    private static final int HTTP_GONE = 410;
    private static final int HTTP_ERROR = 499;

    // <100M的文件 用于下载，事先插入数据和文件目录
    private static final String EXITED_FILE_IN_SIZE_ID = "f8816e8aa6ed46cea199e130736406c9";

    private static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 上传限制大小内的文件，<100M
     */
    @Test
    void uploadFileInSize() {
        final ResponseVo responseVo = FileClient.uploadFile(FILE_IN_SIZE);
        final int code = responseVo.getCode();
        // 请求成功
        Assertions.assertEquals(HTTP_OK, code);
    }

    /**
     * 上传100M的文件
     * 服务端设置最大100m的大小，100m的文件+报文其他信息，应该上传失败。
     * the request was rejected because its size (104857757) exceeds the configured maximum (104857600)
     */
    @Test
    void uploadFileOnSize() {
        final ResponseVo responseVo = FileClient.uploadFile(FILE_ON_SIZE);
        final int code = responseVo.getCode();
        // 请求失败
        Assertions.assertEquals(HTTP_ERROR, code);
    }

    /**
     * 上传>100M的文件
     */
    @Test
    void uploadFileOutSize() {
        final ResponseVo responseVo = FileClient.uploadFile(FILE_OUT_SIZE);
        final int code = responseVo.getCode();
        // 请求失败
        Assertions.assertEquals(code, HTTP_ERROR);
    }

    /**
     * 下载存在的文件
     */
    @Test
    void downloadExistedFile() {
        final ResponseVo responseVo = FileClient.downloadFile(EXITED_FILE_IN_SIZE_ID, SAVE_PATH);
        final int code = responseVo.getCode();
        // 这里message是文件名
        final String message = responseVo.getMessage();
        // 请求成功
        Assertions.assertEquals(HTTP_OK, code);
        // 文件存在
        Assertions.assertTrue(new File(SAVE_PATH + File.separator + message).exists());
    }

    /**
     * 下载不存在的文件
     */
    @Test
    void downloadNonFile() {
        final ResponseVo responseVo = FileClient.downloadFile(getUUID(), SAVE_PATH);
        final int code = responseVo.getCode();
        // 请求失败
        Assertions.assertEquals(HTTP_GONE, code);
    }

    /**
     * 获取存在的文件的信息
     */
    @Test
    void getFileInfo() {
        final ResponseVo responseVo = FileClient.getFileInfo(EXITED_FILE_IN_SIZE_ID);
        final int code = responseVo.getCode();
        final String message = responseVo.getMessage();
        System.out.println(message);
        // 请求成功
        Assertions.assertEquals(HTTP_OK, code);
        Assertions.assertDoesNotThrow(()->{
            JSONObject jsonObject = JSONObject.parseObject(message);
            // 这里需要进一步判断json格式
            Assertions.assertTrue(jsonObject.containsKey("id"));
            Assertions.assertEquals(EXITED_FILE_IN_SIZE_ID, jsonObject.get("id"));
            Assertions.assertTrue(jsonObject.containsKey("fileName"));
            Assertions.assertTrue(jsonObject.containsKey("fileSize"));
            Assertions.assertTrue(jsonObject.containsKey("fileType"));
            Assertions.assertTrue(jsonObject.containsKey("createDate"));
            Assertions.assertTrue(jsonObject.containsKey("fileDir"));
        });

    }

    /**
     * 获取不存在的文件的信息
     */
    @Test
    void getNonFileInfo() {
        final ResponseVo responseVo = FileClient.getFileInfo(getUUID());
        final int code = responseVo.getCode();
        // 请求失败
        Assertions.assertEquals(HTTP_GONE, code);
    }
}
