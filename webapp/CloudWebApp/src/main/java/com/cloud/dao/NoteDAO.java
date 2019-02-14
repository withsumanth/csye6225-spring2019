package com.cloud.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cloud.pojo.Note;
import com.cloud.pojo.User;

@Repository
public interface NoteDAO extends JpaRepository<Note, Long>{
	Note save(Note note);

	Note findByNoteId(String noteId);
	
	List<Note> findByUser(User user);
}
