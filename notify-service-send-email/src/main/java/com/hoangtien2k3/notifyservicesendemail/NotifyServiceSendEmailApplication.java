package com.hoangtien2k3.notifyservicesendemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import javax.mail.*;

@EnableEurekaClient
@SpringBootApplication
public class NotifyServiceSendEmailApplication {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(NotifyServiceSendEmailApplication.class, args);
	}
}
