package com.hoangtien2k3.shippingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class 	ShippingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShippingServiceApplication.class, args);
	}

}
