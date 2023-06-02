package com.geo.miniooss.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Autowired
    private OssProperties ossProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(ossProperties.getEndpoint())
                        .credentials(ossProperties.getAccessKey(), ossProperties.getSecretKey())
                        .region(ossProperties.getRegion())
                        .build();
        return minioClient;
    }
}
