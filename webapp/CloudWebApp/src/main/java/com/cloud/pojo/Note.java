package com.cloud.pojo;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Note {
	
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "noteId", columnDefinition = "VARCHAR(100)")
	private String noteId;
	private String content;
	private String title;
	private String createdOn;
	private String lastUpdatedOn;
	
	@ManyToOne
	@JoinColumn(name="userId")
	private User user;

	public Note() {
		super();
	}
	
	public Note(String noteId, String content, String title, String createdOn, String lastUpdatedOn, User user) {
		super();
		this.noteId = noteId;
		this.content = content;
		this.title = title;
		this.createdOn = createdOn;
		this.lastUpdatedOn = lastUpdatedOn;
		this.user = user;
	}

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(String lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
}
