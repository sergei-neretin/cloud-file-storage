package com.sergeineretin.cloudfilestorage.config;

import io.minio.MinioClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SpringConfig {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    MinioClient minioClient(@Value("${MINIO_ROOT_USER}") String accessKey,
                            @Value("${MINIO_ROOT_PASSWORD}") String secretKey) {
        return MinioClient.builder()
                .endpoint("http://127.0.0.1:9000")
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    @Profile("dev")
    MinioClient minioClientDev() {
        return MinioClient.builder()
                .endpoint("http://127.0.0.1:9000")
                .credentials("username", "password")
                .build();
    }

}
