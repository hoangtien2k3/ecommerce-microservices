package com.hoangtien2k3.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class OrderSerivceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderSerivceApplication.class, args);
	}

}
