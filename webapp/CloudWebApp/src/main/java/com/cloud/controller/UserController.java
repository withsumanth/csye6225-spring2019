package com.cloud.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.pojo.User;
import com.cloud.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;


	@RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes="application/json")
	public ResponseEntity<Map<String,Object>> registerUser(@RequestBody User user) {
		Map<String,Object> m = new HashMap<String,Object>();
		try {
			String emailPattern = "^([a-zA-Z0-9_.+-])+\\@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
			String passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
			if(user == null) {
				m.put("message", "Please send username and password");
				m.put("status",HttpStatus.BAD_REQUEST.toString());
				return new ResponseEntity<Map<String,Object>>(m,HttpStatus.BAD_REQUEST);
			}else if(user.getUserEmail() == null) {
				m.put("message", "Email is null or json format is not correct");
				m.put("status",HttpStatus.NOT_ACCEPTABLE.toString());
				return new ResponseEntity<Map<String,Object>>(m,HttpStatus.NOT_ACCEPTABLE);
			}else if(user.getPassword() == null) {
				m.put("message", "Password is or json format is not correct");
				m.put("status",HttpStatus.NOT_ACCEPTABLE.toString());
				return new ResponseEntity<Map<String,Object>>(m,HttpStatus.NOT_ACCEPTABLE);
			}else {
				Pattern patternForEmail = Pattern.compile(emailPattern);
				Pattern patternForPassword = Pattern.compile(passwordPattern);
				Matcher emailMatcher = patternForEmail.matcher(user.getUserEmail());
				Matcher passwordMatcher = patternForPassword.matcher(user.getPassword());
				if(!emailMatcher.matches()) {
					m.put("message", "Email Pattern is wrong");
					m.put("status",HttpStatus.NOT_ACCEPTABLE.toString());
					return new ResponseEntity<Map<String,Object>>(m,HttpStatus.NOT_ACCEPTABLE);
				}else if(!passwordMatcher.matches()) {
					m.put("message", "Password Pattern is wrong");
					m.put("status",HttpStatus.NOT_ACCEPTABLE.toString());
					return new ResponseEntity<Map<String,Object>>(m,HttpStatus.NOT_ACCEPTABLE);
				}
				User userExists = userService.findByUserEmail(user.getUserEmail());
				if(userExists == null) {
					userService.save(user);
					m.put("message", "account created successfully");
					m.put("email", user.getUserEmail());
					m.put("status",HttpStatus.OK.toString());
					return new ResponseEntity<Map<String,Object>>(m,HttpStatus.OK);
				}else {
					m.put("message", "Username already exists");
					m.put("status",HttpStatus.CONFLICT.toString());
					return new ResponseEntity<Map<String,Object>>(m,HttpStatus.CONFLICT);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String,Object>> loginUser(HttpServletRequest request, HttpServletResponse response) {
		String header = request.getHeader("Authorization");
		Map<String,Object> m = new HashMap<String,Object>();
		if(header!=null && header.contains("Basic")) {
			String userDetails[] = decodeHeader(header);
			User userExists = userService.findByUserEmail(userDetails[0]);
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if(userExists !=null ) {
				if(encoder.matches(userDetails[1], userExists.getPassword())) {
					m.put("message", "Current time is "+new Date());
					m.put("status",HttpStatus.OK.toString());
					return new ResponseEntity<Map<String,Object>>(m,HttpStatus.OK);
				}else {
					m.put("message", "Password entered is wrong");
					m.put("status",HttpStatus.FORBIDDEN.toString());
					return new ResponseEntity<Map<String,Object>>(m,HttpStatus.FORBIDDEN);
				}
			}else {
				m.put("message", "Username does not exist");
				m.put("status",HttpStatus.FORBIDDEN.toString());
				return new ResponseEntity<Map<String,Object>>(m,HttpStatus.FORBIDDEN);
			}
		}else {
			m.put("message", "User is not logged in");
			m.put("status",HttpStatus.UNAUTHORIZED.toString());
			return new ResponseEntity<Map<String,Object>>(m,HttpStatus.CONFLICT);
		}
	}

	private static String[] decodeHeader(final String encoded) {
		assert encoded.substring(0, 6).equals("Basic");
		String basicAuthEncoded = encoded.substring(6);
		String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
		final String[] userDetails = basicAuthAsString.split(":", 2);
		return userDetails;
	}
}
