package com.cloud.service;

import java.io.File;

public interface S3Service {
	public void deleteFile(String keyName);
	public void uploadFile(String keyName, File uploadFile);
}
