package com.cloud.auth;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
	
	@Bean
	public AmazonS3 s3client() {
		
		AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
        		.withRegion(Regions.US_EAST_1)
        		.withCredentials(new DefaultAWSCredentialsProviderChain())
        		.build();
        return amazonS3Client;
	}
}
