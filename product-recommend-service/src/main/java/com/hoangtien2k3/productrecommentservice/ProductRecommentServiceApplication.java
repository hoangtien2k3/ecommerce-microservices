package com.hoangtien2k3.productrecommentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.hoangtien2k3")
public class ProductRecommentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductRecommentServiceApplication.class, args);
	}

}
