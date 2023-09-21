package com.hoangtien2k3.orderserivce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableJpaRepositories
<<<<<<< HEAD
=======
@EnableFeignClients
@EnableWebSecurity
@EnableRedisHttpSession
>>>>>>> 59eb0cc20681e0fdc575684a7cd7a6d1220d661c
public class OrderSerivceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderSerivceApplication.class, args);
	}

}
