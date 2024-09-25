package com.hoangtien2k3.tax;

import com.hoangtien2k3.tax.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class TaxApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxApplication.class, args);
    }
}
