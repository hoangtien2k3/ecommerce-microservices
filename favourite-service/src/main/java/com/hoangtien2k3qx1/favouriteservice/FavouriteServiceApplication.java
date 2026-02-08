package com.hoangtien2k3qx1.favouriteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = "com.hoangtien2k3")
@EnableEurekaClient
public class FavouriteServiceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT +0:00"));
		SpringApplication.run(FavouriteServiceApplication.class, args);
	}

}
