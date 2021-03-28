package com.yuchao.fileserver.controller;

import com.yuchao.fileserver.common.BizException;
import com.yuchao.fileserver.entity.Attachment;
import com.yuchao.fileserver.service.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author yuchao
 * @date 2021/3/26 5:56 下午
 **/
@RestController("/file")
public class FileController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AttachmentService service;

    /**
     * 上传文件
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file){
        try {
            String id = service.uploadFile(file);
            logger.info("文件上传成功,文件id:" + id);
            return id;
        } catch (Exception e) {
            logger.error("上传失败", e);
            return "上传失败。";
        }
    }

    /**
     * 下载文件
     * @param id
     * @param response
     */
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public ResponseEntity<String> downloadFile(@RequestParam("id") String id, HttpServletResponse response){
        try {
            service.getFile(id, response);
            return new ResponseEntity(HttpStatus.OK);
        } catch (BizException e) {
            logger.error(e.getErrorMsg(), e);
            return new ResponseEntity("无法找到该文件",HttpStatus.GONE);
        } catch (IOException e) {
            logger.error("文件下载失败:I/O异常", e);
            return new ResponseEntity("文件下载失败：I/O异常", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 获取文件信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/getFileInfo", method = RequestMethod.GET)
    public ResponseEntity<Attachment> getFileInfo(@RequestParam("id") String id){
        Optional<Attachment> attachment = service.getFileInfo(id);

        if (attachment.isPresent()) {
            Attachment attachment1 = attachment.get();
            logger.info("文件查询成功:" + attachment1.toString());
            return new ResponseEntity(attachment1,HttpStatus.OK);
        } else {
            logger.error("无法查询到文件，id: " + id);
            return new ResponseEntity(HttpStatus.GONE);
        }
    }
}
