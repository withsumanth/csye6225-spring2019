package com.cloud.auth;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
	
	@Value("${aws.region}")
	String region;
	
	@Bean
	public AmazonS3 s3client() {
		
		AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
        		.withRegion(region)
        		.withCredentials(new DefaultAWSCredentialsProviderChain())
        		.build();
        return amazonS3Client;
	}
}
