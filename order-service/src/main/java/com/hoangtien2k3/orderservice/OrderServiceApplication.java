package com.hoangtien2k3.orderservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.stereotype.Service;

@SpringBootApplication
@EnableEurekaClient
@OpenAPIDefinition(
		info = @Info(
				title = "Order-Service Open API",
				version = "1.0.0",
				description = "Order-Service Open API documentation"
		),
		servers = @Server(
				url = "http://localhost:8084",
				description = "Order-Service Open API url"
		)
)
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
