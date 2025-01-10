package com.hoangtien2k3.media;

import com.hoangtien2k3.commonlib.config.CorsConfig;
import com.hoangtien2k3.media.config.hoangtien2k3Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.hoangtien2k3.media", "com.hoangtien2k3.commonlib"})
@EnableConfigurationProperties({hoangtien2k3Config.class, CorsConfig.class})
public class MediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaApplication.class, args);
    }

}
