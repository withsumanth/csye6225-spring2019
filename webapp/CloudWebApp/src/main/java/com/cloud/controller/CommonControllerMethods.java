package com.cloud.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.cloud.pojo.User;
import com.cloud.service.UserService;

public class CommonControllerMethods {
	
	/**
	 * This method decodes the basic authentication in the header using the Base64 decoder to get the username and password
	 * @param encoded
	 * @return
	 */
	protected String[] decodeHeader(final String encoded) {
		assert encoded.substring(0, 6).equals("Basic");
		String basicAuthEncoded = encoded.substring(6);
		String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
		final String[] userDetails = basicAuthAsString.split(":", 2);
		return userDetails;
	}

	//Check for unauthorized user
	protected User checkBadRequest(String header, UserService userService) {
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
	
	protected File convertMultiPartToFile(MultipartFile file) throws IOException {
	    File convFile = new File(file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}
}
