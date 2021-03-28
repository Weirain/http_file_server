package com.yuchao.fileserver.service;

import com.yuchao.fileserver.common.BizException;
import com.yuchao.fileserver.dao.AttachmentDao;
import com.yuchao.fileserver.entity.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * @author yuchao
 * @date 2021/3/26 6:40 下午
 **/
@Service
public class AttachmentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AttachmentDao attachmentDao;

    /**
     * 存放目录
     */
    @Value("${rootdir}")
    private String rootDir;

    /**
     * 获取文件信息
     * @param id
     * @return
     */
    public Optional<Attachment> getFileInfo(String id) {
        Optional<Attachment> attachment = attachmentDao.findById(id);
        return attachment;
    }


    public String uploadFile(MultipartFile file) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        File dir = new File(rootDir, dateStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String shuffix = FilenameUtils.getExtension(file.getOriginalFilename());
        String id = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String newName = id + "." + shuffix;
        File targetFile = new File(dir,newName);
        file.transferTo(targetFile);

        Attachment attachment = new Attachment();
        attachment.setId(id);
        attachment.setFileDir(targetFile.getAbsolutePath().replace("\\", "/").replace(rootDir, ""));
        attachment.setCreateDate(date);
        String filename = file.getOriginalFilename();
        attachment.setFileName(filename);
        attachment.setFileType(shuffix);
        attachment.setContentType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachmentDao.save(attachment);
        return id;
    }

    /**
     * 下载文件
     * @param id
     * @param response
     * @throws BizException
     * @throws IOException
     */
    public void getFile(String id, HttpServletResponse response) throws BizException,IOException {

            Attachment attachment = attachmentDao.findById(id).orElseThrow(()->new BizException("文件下载失败：没有这个文件！"));
            String fileDir = attachment.getFileDir();
            final File file = getFile(fileDir);
            response.setContentLength(attachment.getFileSize().intValue());
            response.setContentType(attachment.getContentType());
            response.setHeader("Content-Disposition","attachment;filename=" + new String(attachment.getFileName().getBytes("UTF-8"), "ISO8859-1"));
            // 下载文件到浏览器
            writefile(response, file);

    }

    /**
     * 获取文件
     * @param fileDir
     * @return
     */
    private File getFile(String fileDir) {
        return new File(this.rootDir,fileDir);
    }


    /**
     * 下载文件
     * @param response
     * @param file
     * @throws IOException
     */
    private void writefile(HttpServletResponse response, File file) throws IOException {
        ServletOutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        int length = (int)file.length();
        try {
            fileInputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();
            // 读取文件字节码
            byte[] data = new byte[length];
            getBytes(fileInputStream, data, length);
            // 将文件流输出到浏览器
            if (data != null) {
                outputStream.write(data);
            }
        }finally{
            try {
                outputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                logger.error("I/O关闭异常", e);
            } finally {
                outputStream = null;
                fileInputStream = null;
            }
        }
    }

    /**
     * 获取字节
     * @param input
     * @param buffer
     * @param length
     * @throws IOException
     */
    private void getBytes(InputStream input, byte[] buffer, int length) throws IOException {
        int remaining;
        int count;
        for(remaining = length; remaining > 0; remaining -= count) {
            int location = length - remaining;
            count = input.read(buffer, location, remaining);
            if (-1 == count) {
                break;
            }
        }
    }
}
