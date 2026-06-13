package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.dto.EmailDetails;
import org.springframework.web.multipart.MultipartFile;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
    String sendMail(MultipartFile[] files, String to, String[] cc, String subject, String body);
}
