package com.cloud.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.dao.UserDAO;
import com.cloud.pojo.User;
import com.cloud.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDAO userDao;

	@Override
	public User save(User u) {
		return userDao.save(u);
	}
}
