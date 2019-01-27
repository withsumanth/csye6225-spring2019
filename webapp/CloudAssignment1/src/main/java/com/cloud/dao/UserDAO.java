package com.cloud.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud.pojo.User;

public interface UserDAO extends JpaRepository<User, Long> {

	User save(User user);


}
