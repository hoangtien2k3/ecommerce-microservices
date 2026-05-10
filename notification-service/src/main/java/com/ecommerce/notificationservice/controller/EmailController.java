package com.ecommerce.notificationservice.controller;

import com.ecommerce.notificationservice.dto.EmailDetails;
import com.ecommerce.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

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
