package com.cloud.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				Pattern pattern = Pattern.compile(emailPattern);
		        Matcher emailMatcher = pattern.matcher(user.getUserEmail());
		        Matcher passwordMatcher = pattern.matcher(user.getPassword());
		        if(!emailMatcher.matches()) {
		        	m.put("message", "Email Pattern is wrong");
		        	m.put("status",HttpStatus.NOT_ACCEPTABLE.toString());
		        	return new ResponseEntity<Map<String,Object>>(m,HttpStatus.NOT_ACCEPTABLE);
		        }else if(!passwordMatcher.matches()) {
		        	m.put("message", "Password Pattern is wrong");
		        	m.put("status",HttpStatus.NOT_ACCEPTABLE.toString());
		        	return new ResponseEntity<Map<String,Object>>(m,HttpStatus.NOT_ACCEPTABLE);
		        }
				userService.save(user);
				m.put("message", "account created successfully");
				m.put("email", user.getUserEmail());
				m.put("status",HttpStatus.OK.toString());
				return new ResponseEntity<Map<String,Object>>(m,HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
