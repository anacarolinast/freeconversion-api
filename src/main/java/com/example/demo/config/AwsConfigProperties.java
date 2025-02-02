package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class AwsConfigProperties {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKeyId;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretAccessKey;

    @Value("${AWS_REGION}")
    private String region;

    @Value("${AWS_S3_BUCKET_NAME}")
    private String s3BucketName;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public String getRegion() {
        return region;
    }

    public String getS3BucketName() {
        return s3BucketName;
    }

    @PostConstruct
    public void printAwsConfig() {
        System.out.println("Access Key: " + accessKeyId);
        System.out.println("Secret Key: " + secretAccessKey);
        System.out.println("Region: " + region);
        System.out.println("Bucket Name: " + s3BucketName);
    }
}
