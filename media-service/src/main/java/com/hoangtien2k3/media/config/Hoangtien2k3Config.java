package com.hoangtien2k3.media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Hoangtien2k3Config {
    
    @Value("${app.public-url:http://localhost:8080}")
    private String publicUrl;
    
    public String publicUrl() {
        return publicUrl;
    }
}