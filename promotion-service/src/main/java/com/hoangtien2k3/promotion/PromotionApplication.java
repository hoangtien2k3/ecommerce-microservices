package com.hoangtien2k3.promotion;

import com.hoangtien2k3.promotion.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class PromotionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromotionApplication.class, args);
    }
}
