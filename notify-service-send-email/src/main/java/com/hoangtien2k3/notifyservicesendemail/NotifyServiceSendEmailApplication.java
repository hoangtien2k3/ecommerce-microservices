package com.hoangtien2k3.notifyservicesendemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@SpringBootApplication
public class NotifyServiceSendEmailApplication {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(NotifyServiceSendEmailApplication.class, args);
	}
}
