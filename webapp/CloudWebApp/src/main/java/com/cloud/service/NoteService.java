package com.cloud.service;

import java.util.List;

import com.cloud.pojo.Note;
import com.cloud.pojo.User;

public interface NoteService {
	
	Note save(Note note, User user);
	
	Note findByNoteId(String noteId);
	
	List<Note> findByUser(User user);
}
