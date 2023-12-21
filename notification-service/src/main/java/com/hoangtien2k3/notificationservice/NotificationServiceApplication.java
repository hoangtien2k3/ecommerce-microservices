package com.hoangtien2k3.notificationservice;

import com.hoangtien2k3.notificationservice.even.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.TimeZone;

@Slf4j
@EnableEurekaClient
@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT +0:00"));
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@KafkaListener(topics = "notificationTopic")
	public void handleNotification(OrderEvent orderPlacedEvent) {
		// send out an email notification
		log.info("Received Notification for Order - {}", orderPlacedEvent.getOrderNumber());
	}

}
