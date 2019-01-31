package com.cloud;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.cloud.controller.UserController;
import com.cloud.pojo.User;
import com.cloud.service.UserService;

@SpringBootTest
@DataJpaTest
@WebMvcTest(controllers = UserController.class, secure = false)
public class CloudWebAppTest {

	@Mock
	UserService userService;

	@Autowired
	MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		User createUser = new User();
		createUser.setUserEmail("withsumanth92@gmail.com");
		createUser.setPassword("Password2#");
		when(userService.findByUserEmail("withsumanth92@gmail.com")).thenReturn(createUser);
	}

	@Test
	public void register() {
		User receivedUser = userService.findByUserEmail("withsumanth92@gmail.com");
		assertEquals(receivedUser.getUserEmail(), "withsumanth92@gmail.com");
	}
}
