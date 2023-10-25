package com.hoangtien2k3.notifyservicesendemail.service;

import com.hoangtien2k3.notifyservicesendemail.entity.EmailDetails;

public interface EmailService {
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}
