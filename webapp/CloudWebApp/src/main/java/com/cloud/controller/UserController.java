package com.cloud.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.cloud.pojo.User;
import com.cloud.service.UserService;
import com.timgroup.statsd.StatsDClient;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private StatsDClient statsDClient;

	private static final CommonControllerMethods methods = new CommonControllerMethods();
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);

	/**
	 * This method handles the call to path /user/registration. It validates email
	 * and password and returns 401 is either doesn't matches requirements. Password
	 * must meet requirements as mentioned by NIST Checks for used already exits and
	 * responds with HTTP status code 409(CONFLICT) Adds new user to the database if
	 * all checks have passed
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
	public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
		statsDClient.incrementCounter("endpoint.createuser.http.post");
		Map<String, Object> m = new HashMap<String, Object>();
		try {
			String emailPattern = "^([a-zA-Z0-9_.+-])+\\@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
			String passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
			if (user == null) {
				m.put("message", "Please send username and password");
				m.put("status", HttpStatus.BAD_REQUEST.toString());
				logger.info("Please send username and password - BAD_REQUEST " + UserController.class);
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
			} else if (user.getUserEmail() == null) {
				m.put("message", "Email is null or json format is not correct");
				m.put("status", HttpStatus.BAD_REQUEST.toString());
				logger.info("Email is null or json format is not correct - BAD_REQUEST " + UserController.class);
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
			} else if (user.getPassword() == null) {
				m.put("message", "Password is null or json format is not correct");
				m.put("status", HttpStatus.BAD_REQUEST.toString());
				logger.info("Password is null or json format is not correct - BAD_REQUEST " + UserController.class);
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
			} else {
				Pattern patternForEmail = Pattern.compile(emailPattern);
				Pattern patternForPassword = Pattern.compile(passwordPattern);
				Matcher emailMatcher = patternForEmail.matcher(user.getUserEmail());
				Matcher passwordMatcher = patternForPassword.matcher(user.getPassword());
				if (!emailMatcher.matches()) {
					m.put("message", "Invalid Email address");
					m.put("status", HttpStatus.BAD_REQUEST.toString());
					logger.info("Invalid Email address BAD_REQUEST " + UserController.class);
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
				} else if (!passwordMatcher.matches()) {
					m.put("message",
							new String[] { "Password requirement:", "Minimum Password Length 8 ",
									"Include Symbols (!@#$)", "Include Numbers (0-9)", "Include Lowercase (abc)",
									"Include Uppercase (ABC)", "Exclude Duplicate Characters",
									"Exclude Similar (iI1loO0)", "Include specific characters" });
					m.put("status", HttpStatus.BAD_REQUEST.toString());
					logger.info("Invalid password format BAD_REQUEST " + UserController.class);
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
				}
				User userExists = userService.findByUserEmail(user.getUserEmail());
				if (userExists == null) {
					userService.save(user);
					m.put("message", "account created successfully");
					m.put("status", HttpStatus.CREATED.toString());
					logger.info("account created successfully - CREATED " + UserController.class);
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.CREATED);
				} else {
					m.put("message", "User already exists");
					m.put("status", HttpStatus.CONFLICT.toString());
					logger.info("User already exists - CONFLICT " + UserController.class);
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.CONFLICT);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method handles calls to / and authenticates the user using basic
	 * authentication If the user is authenticated, a response with the current time
	 * if the user is not authenticated, a response with HTTP status code 401
	 * (Unauthorized)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> loginUser(HttpServletRequest request, HttpServletResponse response) {
		statsDClient.incrementCounter("endpoint.getuser.http.get");
		String header = request.getHeader("Authorization");
		Map<String, Object> m = new HashMap<String, Object>();
		if (header != null && header.contains("Basic")) {
			String userDetails[] = methods.decodeHeader(header);
			User userExists = userService.findByUserEmail(userDetails[0]);
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if (userExists != null) {
				if (encoder.matches(userDetails[1], userExists.getPassword())) {
					m.put("message", "Current time is " + new Date());
					m.put("status", HttpStatus.OK.toString());
					logger.info("Logged In - OK " + UserController.class);
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.OK);
				} else {
					m.put("message", "Username/password is incorrect");
					logger.info("Username/password is incorrect - UNAUTHORIZED " + UserController.class);
					m.put("status", HttpStatus.UNAUTHORIZED.toString());
					return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
				}
			} else {
				m.put("message", "Username does not exist");
				m.put("status", HttpStatus.UNAUTHORIZED.toString());
				return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
			}
		} else {
			m.put("message", "User is not logged in");
			m.put("status", HttpStatus.UNAUTHORIZED.toString());
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/reset", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody User user) {
		statsDClient.incrementCounter("endpoint.resetPassword.http.post");
		JSONObject jsonBody=new JSONObject(body);
		Map<String, Object> m = new HashMap<String, Object>();
		User userExists = userService.findByUserEmail(user.getUserEmail());
		if (userExists != null) {
			AmazonSNS sns = AmazonSNSClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain()).build();
			String topic = sns.createTopic("password_reset").getTopicArn();
			String emailJson = "{ \"email\":\""+user.getUserEmail()+"\"}";
			PublishRequest pubRequest = new PublishRequest(topic, emailJson);
			sns.publish(pubRequest);
			logger.info("Email sent successfully - CREATED " + UserController.class);
			m.put("status", HttpStatus.CREATED.toString());
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.CREATED);
		} else {
			m.put("message", "Username does not exist");
			logger.info("Username does not exist - BAD_REQUEST " + UserController.class);
			return new ResponseEntity<Map<String, Object>>(m, HttpStatus.BAD_REQUEST);
		}
	}
}
