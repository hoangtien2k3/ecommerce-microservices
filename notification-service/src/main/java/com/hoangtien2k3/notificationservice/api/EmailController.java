package com.hoangtien2k3.notificationservice.api;

import com.hoangtien2k3.notificationservice.dto.EmailDetails;
import com.hoangtien2k3.notificationservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendSimpleMail")
    public Mono<String> sendSimpleMail(@RequestBody EmailDetails details) {
        return emailService.sendSimpleMail(details);
    }

    @PostMapping("/sendMailWithAttachment")
    public Mono<String> sendMailWithAttachment(@RequestBody EmailDetails details) {
        return emailService.sendMailWithAttachment(details);
    }

    @PostMapping("/sendMail")
    public Mono<String> sendMail(@RequestParam(value = "file", required = false) MultipartFile[] files,
                                 @RequestParam String to,
                                 @RequestParam String[] cc,
                                 @RequestParam String subject,
                                 @RequestParam String body) {
        return emailService.sendMail(files, to, cc, subject, body);
    }

}
