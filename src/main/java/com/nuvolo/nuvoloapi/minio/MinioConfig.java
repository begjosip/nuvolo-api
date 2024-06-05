package com.nuvolo.nuvoloapi.minio;


import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioComponent minioComponent;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioComponent.getUrl())
                .credentials(minioComponent.getAccessKey(), minioComponent.getAccessSecret())
                .build();
    }
}
