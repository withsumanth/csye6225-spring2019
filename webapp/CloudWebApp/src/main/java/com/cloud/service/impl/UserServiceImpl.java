package com.cloud.service.impl;

import org.mindrot.jbcrypt.BCrypt;
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
		User user = new User();
		user.setId(u.getId());
		user.setUserEmail(u.getUserEmail());
		user.setPassword(BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));
		return userDao.save(user);
	}

	@Override
	public User findByUserEmail(String userEmail) {
		return userDao.findByUserEmail(userEmail);
	}
}
