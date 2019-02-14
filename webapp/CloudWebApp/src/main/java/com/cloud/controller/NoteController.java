package com.cloud.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.dao.NoteDAO;
import com.cloud.pojo.Note;
import com.cloud.pojo.User;
import com.cloud.service.NoteService;
import com.cloud.service.UserService;

@RestController
public class NoteController {

	@Autowired
	private UserService userService;
	@Autowired
	private NoteService noteService;
	@Autowired
	NoteDAO noteDao;

	@RequestMapping(value = "/note", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
	public ResponseEntity<Map<String, Object>> registerNote(@RequestBody Note note, HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		User user = checkBadRequest(header);
		if (user != null) {
			if(note.getContent() == null || note.getTitle() == null || note.getContent().isEmpty() || note.getTitle().isEmpty()) {
				m.put("message", "Invalid Note title/content");
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
			}
			Note createdNoted = noteService.save(note, user);
			m.put("id", createdNoted.getNoteId());
			m.put("content", createdNoted.getContent());
			m.put("title", createdNoted.getTitle());
			m.put("created_on", createdNoted.getCreatedOn());
			m.put("last_updated_on", createdNoted.getLastUpdatedOn());
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.CREATED);
		} else {
			m.put("message", "Username/password is incorrect");
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@RequestMapping(value = "/note/{noteId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> getNote(@PathVariable String noteId, HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		User user = checkBadRequest(header);
		if (user != null) {
			Note note = noteDao.findByNoteId(noteId);
			if(note == null) {
				m.put("message", "There is no note for given id");
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.NOT_FOUND);
			}else {
				m.put("id", note.getNoteId());
				m.put("content", note.getContent());
				m.put("title", note.getTitle());
				m.put("created_on", note.getCreatedOn());
				m.put("last_updated_on", note.getLastUpdatedOn());
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.OK);
			}
		} else {
			m.put("message", "Username/password is incorrect");
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@RequestMapping(value = "/note/{noteId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
	public ResponseEntity<Map<String, Object>> updateNote(@PathVariable String noteId,@RequestBody Note note, HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		User user = checkBadRequest(header);
		if (user != null) {
			Note notetoBeUpdated = noteDao.findByNoteId(noteId);
			if(notetoBeUpdated == null ) {
				m.put("message", "There is no note for given id");
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
			}else {
				if(note.getContent() == null && note.getTitle() == null ) {
					m.put("message", "Invalid Note title/content");
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
				}else {
					if(note.getContent()!=null)
						notetoBeUpdated.setContent(note.getContent());
					if(note.getTitle()!=null)
						notetoBeUpdated.setTitle(note.getTitle());
					Date d = new Date();
					SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					notetoBeUpdated.setLastUpdatedOn(f.format(d));
					noteDao.save(notetoBeUpdated);
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.NO_CONTENT);
				}
			}
		} else {
			m.put("message", "Username/password is incorrect");
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@RequestMapping(value = "/note/{noteId}", method = RequestMethod.DELETE)
	public ResponseEntity<Map<String, Object>> deleteNote(@PathVariable String noteId, HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		User user = checkBadRequest(header);
		if (user != null) {
			Note note = noteDao.findByNoteId(noteId);
			if(note == null) {
				m.put("message", "There is no note for given id");
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
			}else {
				noteDao.delete(note);
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.NO_CONTENT);
			}
		} else {
			m.put("message", "Username/password is incorrect");
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@RequestMapping(value = "/note", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Map<String, Object>>> registerNote(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		User user = checkBadRequest(header);
		if (user != null) {
			List<Note> notes = noteService.findByUser(user);
			for(Note n:notes) {
				LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("id",n.getNoteId());
				map.put("content",n.getContent());
				map.put("title",n.getTitle());
				map.put("created_on",n.getCreatedOn());
				map.put("last_updated_on",n.getLastUpdatedOn());
				mapList.add(map);
			}
			return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.OK);
		} else {
			LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
			m.put("message", "Username/password is incorrect");
			mapList.add(m);
			return new ResponseEntity<List<Map<String, Object>>>(mapList, HttpStatus.UNAUTHORIZED);
		}
	}

	private User checkBadRequest(String header) {
		if (header != null && header.contains("Basic")) {
			String userDetails[] = decodeHeader(header);
			User userExists = userService.findByUserEmail(userDetails[0]);
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if (userExists != null) {
				if (encoder.matches(userDetails[1], userExists.getPassword())) {
					return userExists;
				} 
			} 
		} 
		return null;
	}

	private static String[] decodeHeader(final String encoded) {
		assert encoded.substring(0, 6).equals("Basic");
		String basicAuthEncoded = encoded.substring(6);
		String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
		final String[] userDetails = basicAuthAsString.split(":", 2);
		return userDetails;
	}
}
