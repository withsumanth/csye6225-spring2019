package com.cloud.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.dao.NoteDAO;
import com.cloud.pojo.Note;
import com.cloud.pojo.User;
import com.cloud.service.NoteService;

@Service("noteService")
public class NoteServiceImpl implements NoteService{
	@Autowired
	NoteDAO noteDao;

	@Override
	public Note save(Note note, User user) {
		Note n = new Note();
		n.setTitle(note.getTitle());
		n.setContent(note.getContent());
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		n.setCreatedOn(f.format(d));
		n.setLastUpdatedOn(f.format(d));
		n.setUser(user);
		return noteDao.save(n);
	}
	
	public Note findByNoteId(String noteId) {
		return noteDao.findByNoteId(noteId);
	}
	
	public List<Note> findByUser(User user){
		return noteDao.findByUser(user);
	}
}
