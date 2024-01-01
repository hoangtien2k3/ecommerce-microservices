package com.hoangtien2k3.notificationservice.service;

import com.hoangtien2k3.notificationservice.dto.EmailDetails;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface EmailService {
    // To send a simple email
    Mono<String> sendSimpleMail(EmailDetails details);

    // To send an email with attachment
    Mono<String> sendMailWithAttachment(EmailDetails details);

    // send email file
    Mono<String> sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body);
}
