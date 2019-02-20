package com.cloud.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cloud.pojo.Attachment;
import com.cloud.pojo.Note;
import com.cloud.pojo.User;

@Repository
public interface AttachmentDAO extends JpaRepository<Attachment, Long>{
	
	Attachment save(Attachment attachment);
	
	Attachment findByAttachmentIdAndNote(String attatchmentId, Note note);
	
	List<Attachment> findByNote(Note note);

}
