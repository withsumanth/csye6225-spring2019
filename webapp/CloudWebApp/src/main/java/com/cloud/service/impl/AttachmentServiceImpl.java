package com.cloud.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.dao.AttachmentDAO;
import com.cloud.pojo.Attachment;
import com.cloud.pojo.Note;
import com.cloud.service.AttachmentService;

@Service("attachmentService")
public class AttachmentServiceImpl implements AttachmentService{

	@Autowired
	AttachmentDAO attachmentDao;
	
	public List<Attachment> findByNote(Note note){
		return attachmentDao.findByNote(note);
	}
}
