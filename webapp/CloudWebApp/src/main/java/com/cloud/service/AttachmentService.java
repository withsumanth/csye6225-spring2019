package com.cloud.service;

import java.util.List;

import com.cloud.pojo.Attachment;
import com.cloud.pojo.Note;

public interface AttachmentService {
	List<Attachment> findByNote(Note note);
}
