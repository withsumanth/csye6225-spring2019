package com.cloud.service;

import java.util.List;

import com.cloud.pojo.Note;
import com.cloud.pojo.User;

public interface NoteService {
	
	Note save(Note note, User user);
	
	Note findByNoteIdAndUser(String noteId, User user);
	
	List<Note> findByUser(User user);
}
