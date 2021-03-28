package com.yuchao.fileserver.dao;

import com.yuchao.fileserver.entity.Attachment;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yuchao
 * @date 2021/3/26 6:45 下午
 **/
@Repository
public interface AttachmentDao extends JpaRepository<Attachment, String>{
}
