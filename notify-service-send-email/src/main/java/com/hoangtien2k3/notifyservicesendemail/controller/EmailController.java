package com.hoangtien2k3.notifyservicesendemail.controller;

import com.hoangtien2k3.notifyservicesendemail.entity.EmailDetails;
import com.hoangtien2k3.notifyservicesendemail.service.EmailService;
import com.hoangtien2k3.notifyservicesendemail.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // Sending a simple Email
    @PostMapping("/sendMail")
    public String sendMail(@RequestBody EmailDetails details) {
        String status = emailService.sendSimpleMail(details);

        return status;
    }

    // Sending email with attachment
    @PostMapping("/sendMailWithAttachment")
    public String sendMailWithAttachment(@RequestBody EmailDetails details) {
        String status = emailService.sendMailWithAttachment(details);

        return status;
    }

    @PostMapping("/send")
    public String sendMail(@RequestParam(value = "file", required = false) MultipartFile[] file,
                           String to,
                           String[] cc,
                           String subject,
                           String body) {
        return emailService.sendMail(file, to, cc, subject, body);
    }

}
