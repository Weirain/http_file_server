package com.yuchao.fileclient.client;

import com.yuchao.fileclient.model.ResponseVo;
import com.yuchao.fileclient.util.HttpUtil;

import java.io.File;

/**
 * 封装成方法
 * @author yuchao
 * @date 2021/3/27 11:20 上午
 **/
public class FileClient {

    final static String ROOT_URL = "http://localhost:8080/";

    final static String GET_FILE_INFO_URL = "getFileInfo?id=";

    final static String UPLOAD_FILE_URL = "uploadFile";

    final static String DOWNLOAD_FILE_URL = "downloadFile?id=";

    /**
     * 上传文件
     * @param filePath
     * @return
     */
    public static ResponseVo uploadFile(String filePath) {
        final String url = ROOT_URL + UPLOAD_FILE_URL;
        final File file = new File(filePath);
        final ResponseVo response = HttpUtil.doPost(url, file);
        return response;
    }

    public static ResponseVo downloadFile(String id, String savePath) {
        final String url = ROOT_URL + DOWNLOAD_FILE_URL + id;
        final ResponseVo response = HttpUtil.doGet(url, savePath);
        return response;
    }

    /**
     * 获取文件信息
     * @param id
     * @return
     */
    public static ResponseVo getFileInfo(String id) {
        final String url = ROOT_URL + GET_FILE_INFO_URL + id;
        ResponseVo s = HttpUtil.doGet(url);
        final int code = s.getCode();
        if (code == 410) {
            s.setMessage("410 Gone: 无法找到文件");
        }
        return s;
    }
}
