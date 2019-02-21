package com.cloud.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Attachment {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "attachmentId", columnDefinition = "VARCHAR(100)")
	private String attachmentId;
	private String attachmentUrl;
	private String attachmentFileName;
	private String attachmentExtension;
	private String attachmentSize;

	@ManyToOne
	@JoinColumn(name = "noteId")
	private Note note;

	public Attachment() {
		super();
	}

	public String getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	public String getAttachmentExtension() {
		return attachmentExtension;
	}

	public void setAttachmentExtension(String attachmentExtension) {
		this.attachmentExtension = attachmentExtension;
	}

	public String getAttachmentSize() {
		return attachmentSize;
	}

	public void setAttachmentSize(String attachmentSize) {
		this.attachmentSize = attachmentSize;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}
}
