package com.example.demo.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    private final AwsConfigProperties awsConfigProperties;

    public AwsConfig(AwsConfigProperties awsConfigProperties) {
        this.awsConfigProperties = awsConfigProperties;
    }

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                awsConfigProperties.getAccessKeyId(),
                awsConfigProperties.getSecretAccessKey()
        );

        return AmazonS3ClientBuilder.standard()
                .withRegion(awsConfigProperties.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
