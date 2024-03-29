package com.cloud.controller;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.cloud.dao.AttachmentDAO;
import com.cloud.dao.NoteDAO;
import com.cloud.pojo.Attachment;
import com.cloud.pojo.Note;
import com.cloud.pojo.User;
import com.cloud.service.AttachmentService;
import com.cloud.service.UserService;
import com.cloud.service.impl.S3ServiceImpl;
import com.timgroup.statsd.StatsDClient;

@Profile("dev")
@RestController
public class AttachmentS3Controller {
	@Autowired
	private UserService userService;
	@Autowired
	NoteDAO noteDao;
	@Autowired
	AttachmentDAO attachmentDao;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private StatsDClient statsDClient;
	
	@Autowired
	private AmazonS3 s3Client;
	
	@Autowired
	private S3ServiceImpl s3ServiceImpl;
	
	@Value("${bucket.name}")
	private String bucketName;

	private static final String uploadingdir = "/uploadingdir/";

	private static final CommonControllerMethods methods = new CommonControllerMethods();
	private final static Logger logger = LoggerFactory.getLogger(AttachmentS3Controller.class);

	// Add attachment to database
	@RequestMapping(value = "/note/{noteId}/attachments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = {
			"multipart/form-data" })
	public ResponseEntity<List<Map<String, Object>>> registerAttachment(
			@RequestParam(value = "file") MultipartFile[] files, HttpServletRequest request,
			@PathVariable String noteId) {
		statsDClient.incrementCounter("endpoint.addattachment.http.post");
		String header = request.getHeader("Authorization");
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		User user = methods.checkBadRequest(header, userService);
		Note note = noteDao.findByNoteIdAndUser(noteId, user);
		if (user != null && note != null) {
			if(files.length > 0) {
				try {
					for (MultipartFile uploadedFile : files) {
						LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
						String key = Instant.now().getEpochSecond() + "_" + uploadedFile.getOriginalFilename();
						String url = "https://s3.amazonaws.com/"+bucketName+"/"+URLEncoder.encode(key,"UTF-8");
	                    File file = methods.convertMultiPartToFile(uploadedFile);
	                    String[] split = uploadedFile.getOriginalFilename().split("\\.");
					    String ext = split[split.length - 1];
	                    s3ServiceImpl.uploadFile(key,file);
						Attachment a = new Attachment();
						a.setAttachmentUrl(url);
						a.setNote(note);
						a.setAttachmentExtension(ext);
						a.setAttachmentFileName(uploadedFile.getOriginalFilename());
						a.setAttachmentSize(String.valueOf(uploadedFile.getSize()));
						Attachment added = attachmentDao.save(a);
						m.put("id", added.getAttachmentId());
						m.put("url", added.getAttachmentUrl());
						mapList.add(m);
					}
					logger.info("Attachment Created - CREATED "+ AttachmentS3Controller.class);
					return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.CREATED);
				} catch (Exception e) {
					LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
					m.put("message", "Error in the file " + e);
					mapList.add(m);
					logger.error("Error in adding file Created - UNPROCESSABLE_ENTITY "+ AttachmentS3Controller.class);
					return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.UNPROCESSABLE_ENTITY);
				}
			}else {
				LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
				m.put("message", "Please add a file");
				mapList.add(m);
				return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.UNAUTHORIZED);
			}
		} else {
			LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
			m.put("message", "Username/password/note id is incorrect");
			mapList.add(m);
			logger.info("Username/password/note id is incorrect - UNAUTHORIZED "+ AttachmentS3Controller.class);
			return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.UNAUTHORIZED);
		}
	}

	// Delete an attachment
	@RequestMapping(value = "/note/{noteId}/attachments/{attId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> deleteAttachment(HttpServletRequest request, @PathVariable String noteId,
			@PathVariable String attId) {
		statsDClient.incrementCounter("endpoint.deleteattachment.http.delete");
		String header = request.getHeader("Authorization");
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		User user = methods.checkBadRequest(header, userService);
		if (user != null) {
			Note note = noteDao.findByNoteIdAndUser(noteId, user);
			if (note != null) {
				Attachment att = attachmentDao.findByAttachmentIdAndNote(attId, note);
				if (att != null) {
					try {
						URL url = new URL(URLDecoder.decode(att.getAttachmentUrl(), "UTF-8"));
						String path = url.getPath();
						s3ServiceImpl.deleteFile(path.split("/")[2]);
						attachmentDao.delete(att);
						logger.info("Attachment Deleted - NO_CONTENT "+ AttachmentS3Controller.class);
						return new ResponseEntity<Map<String, Object>>(m, HttpStatus.NO_CONTENT);
					} catch (Exception e) {
						m.put("message", "Error in thr file " + e);
						return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNPROCESSABLE_ENTITY);
					}
				} else {
					logger.info("There is no attachment for id - BAD_REQUEST "+ AttachmentS3Controller.class);
					m.put("message", "There is no attachment for given id");
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
				}
			} else {
				m.put("message", "There is no note for given id");
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
			}
		} else {
			m.put("message", "Username/password/NoteId is incorrect");
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
		}
	}

	// Update an attachment
	@RequestMapping(value = "/note/{noteId}/attachments/{attId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = {
			"multipart/form-data" })
	public ResponseEntity<Map<String, Object>> updateAttachment(HttpServletRequest request, @PathVariable String noteId,
			@PathVariable String attId, @RequestParam(value = "file") MultipartFile[] files) {
		String header = request.getHeader("Authorization");
		statsDClient.incrementCounter("endpoint.updateattachment.http.put");
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		User user = methods.checkBadRequest(header, userService);
		if (user != null) {
			Note note = noteDao.findByNoteIdAndUser(noteId, user);
			if (note != null && files.length > 0) {
				if (files.length < 2) {
					Attachment att = attachmentDao.findByAttachmentIdAndNote(attId, note);
					if (att != null) {
						try {
							URL url = new URL(URLDecoder.decode(att.getAttachmentUrl(), "UTF-8"));
							String path = url.getPath();
							s3ServiceImpl.deleteFile(path.split("/")[2]);
							String key = Instant.now().getEpochSecond() + "_" + files[0].getOriginalFilename();
							String urlUpdated = "https://s3.amazonaws.com/"+bucketName+"/"+URLEncoder.encode(key,"UTF-8");
		                    File file = methods.convertMultiPartToFile(files[0]);
		                    s3ServiceImpl.uploadFile(key,file);
		                    String[] split = files[0].getOriginalFilename().split("\\.");
						    String ext = split[split.length - 1];
							att.setAttachmentUrl(urlUpdated);
							att.setAttachmentExtension(ext);
							att.setAttachmentFileName(files[0].getOriginalFilename());
							att.setAttachmentSize(String.valueOf(files[0].getSize()));
							attachmentDao.save(att);
							logger.info("Updated Attachment- NO_CONTENT "+ AttachmentS3Controller.class);
							return new ResponseEntity<Map<String, Object>>(m, HttpStatus.NO_CONTENT);
						} catch (Exception e) {
							m.put("message", "Error in thr file " + e);
							logger.error("Error in adding file Created - UNPROCESSABLE_ENTITY "+ AttachmentS3Controller.class);
							return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNPROCESSABLE_ENTITY);
						}
					} else {
						m.put("message", "There is no attachment for given id");
						return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
					}
				} else {
					m.put("message", "Please attach single file");
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNPROCESSABLE_ENTITY);
				}
			} else {
				m.put("message", "There is no note for given id/Attachment is not added");
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
			}
		} else {
			m.put("message", "Username/password/NoteId is incorrect");
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
		}
	}

	// Get all the attachments of a Note
	@RequestMapping(value = "/note/{noteId}/attachments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Map<String, Object>>> registerNote(HttpServletRequest request,
			@PathVariable String noteId) {
		String header = request.getHeader("Authorization");
		statsDClient.incrementCounter("endpoint.getattachment.http.get");
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		User user = methods.checkBadRequest(header, userService);
		if (user != null) {
			Note note = noteDao.findByNoteIdAndUser(noteId, user);
			if (note != null) {
				List<Attachment> atts = attachmentService.findByNote(note);
				for (Attachment a : atts) {
					LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
					map.put("id", a.getAttachmentId());
					map.put("content", a.getAttachmentUrl());
					mapList.add(map);
				}
				logger.info("Get Attachment- OK "+ AttachmentS3Controller.class);
				return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.OK);
			} else {
				LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
				m.put("message", "There is no note for given id");
				mapList.add(m);
				return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.BAD_REQUEST);
			}
		} else {
			LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
			m.put("message", "Username/password is incorrect");
			mapList.add(m);
			return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.UNAUTHORIZED);
		}
	}
}
