package com.hoangtien2k3.notifyservicesendemail.service;

import com.hoangtien2k3.notifyservicesendemail.entity.EmailDetails;
import org.springframework.web.multipart.MultipartFile;

public interface EmailService {
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);

    // send email file
    String sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body);
}
