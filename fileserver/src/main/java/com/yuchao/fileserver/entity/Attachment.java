package com.yuchao.fileserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件实体类
 * @author yuchao
 * @date 2021/3/26 6:34 下午
 **/
@Table(name = "attachment")
@Entity(name = "attachment")
public class Attachment implements Serializable {
    // 主键uuid
    @Id
    @Column(name = "id")
    String id;
    // 文件名
    @Column(name = "file_name")
    String fileName;
    // 文件大小
    @Column(name = "file_size")
    Long fileSize;
    // 文件类型
    @Column(name = "file_type")
    String fileType;
    // 创建日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "create_date")
    Date createDate;
    // 文件存放目录
    @Column(name = "file_dir")
    String fileDir;
    // Content-type
    @JsonIgnore
    @Column(name = "content_type")
    String contentType;


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id='" + id + '\'' +
                ", realName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", fileTye='" + fileType + '\'' +
                ", createDate=" + createDate +
                ", fileDir='" + fileDir + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }
}
