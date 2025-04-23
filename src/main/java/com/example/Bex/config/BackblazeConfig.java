package com.example.Bex.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;  // <- обязательно

@Configuration
public class BackblazeConfig {

    @Value("${b2.access-key-id}")
    private String accessKeyId;
    @Value("${b2.secret-access-key}")
    private String secretAccessKey;
    @Value("${b2.endpoint}")
    private String endpoint;
    @Value("${b2.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        ClientConfiguration clientConfig = new ClientConfiguration()
                .withProtocol(Protocol.HTTPS)
                .withSignerOverride("AWSS3V4SignerType");

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(endpoint, region))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfig)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }
}
