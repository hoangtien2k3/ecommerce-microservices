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

//		Properties props = new Properties();
//		props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
//		props.put("mail.smtp.socketFactory.port", "587"); //SSL Port
//		props.put("mail.smtp.socketFactory.class",
//				"javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
//		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
//		props.put("mail.smtp.port", "587"); //SMTP Port
//		props.put("mail.smtp.starttls.enable", "true");
//
//		Authenticator auth = new Authenticator() {
//			//override the getPasswordAuthentication method
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("hoangtien2k3dev@gmail.com", "Aa0828007853Aa#");
//			}
//		};
//
//		Session session = Session.getDefaultInstance(props, auth);
//		System.out.println("Session created");
//		sendEmail(session, "hoangtien2k3qx1@gmail.com","SSLEmail Testing Subject", "SSLEmail Testing Body");


//		String to = "hoangtien2k3qx1@gmail.com";
//		String subject = "subject";
//		String msg ="email text....";
//		final String from ="hoangtien2k3dev@gmail.com";
//		final  String password ="123456#";
//
//		Properties props = new Properties();
//		props.setProperty("mail.transport.protocol", "smtp");
//		props.setProperty("mail.host", "smtp.gmail.com");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.port", "587");
//		props.put("mail.debug", "true");
//		props.put("mail.smtp.socketFactory.port", "587");
//		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.socketFactory.fallback", "false");
//		Session session = Session.getDefaultInstance(props,
//				new javax.mail.Authenticator() {
//					protected PasswordAuthentication getPasswordAuthentication() {
//						return new PasswordAuthentication(from, password);
//					}
//				});
//
//		//session.setDebug(true);
//		Transport transport = session.getTransport();
//		InternetAddress addressFrom = new InternetAddress(from);
//
//		MimeMessage message = new MimeMessage(session);
//		message.setSender(addressFrom);
//		message.setSubject(subject);
//		message.setContent(msg, "text/plain");
//		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//
//		transport.connect();
//		Transport.send(message);
//		transport.close();
//	}


//	public static void sendEmail(Session session, String toEmail, String subject, String body) {
//		try {
//			MimeMessage msg = new MimeMessage(session);
//			//set message headers
//			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
//			msg.addHeader("format", "flowed");
//			msg.addHeader("Content-Transfer-Encoding", "8bit");
//
//			msg.setFrom(new InternetAddress("no_reply@example.com", "NoReply-JD"));
//
//			msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));
//
//			msg.setSubject(subject, "UTF-8");
//
//			msg.setText(body, "UTF-8");
//
//			msg.setSentDate(new Date());
//
//			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
//			System.out.println("Message is ready");
//			Transport.send(msg);
//
//			System.out.println("EMail Sent Successfully!!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	}
