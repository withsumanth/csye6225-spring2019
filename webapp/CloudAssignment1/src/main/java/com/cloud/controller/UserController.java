package com.cloud.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = "application/json")
	public User registerUser(@RequestBody User user) {
		JSONObject jsonObj = new JSONObject();
		try {
			User createNewUser = new User();
			createNewUser.setPassword(user.getPassword());
			createNewUser.setUserEmail(user.getUserEmail());
			userService.save(createNewUser);
			jsonObj.put("message", "account created successfully");
			jsonObj.put("email", user.getUserEmail());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;

	}
}
