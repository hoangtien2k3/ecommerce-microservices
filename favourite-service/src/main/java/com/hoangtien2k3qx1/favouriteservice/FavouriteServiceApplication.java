package com.hoangtien2k3qx1.favouriteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class FavouriteServiceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT +0:00"));
		SpringApplication.run(FavouriteServiceApplication.class, args);
	}

}
