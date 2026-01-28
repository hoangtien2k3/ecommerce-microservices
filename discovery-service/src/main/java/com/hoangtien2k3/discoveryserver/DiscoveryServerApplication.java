package com.hoangtien2k3.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT +0:00"));
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }

}




