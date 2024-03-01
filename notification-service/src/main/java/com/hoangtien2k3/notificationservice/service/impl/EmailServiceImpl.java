package com.hoangtien2k3.notificationservice.service.impl;

import com.hoangtien2k3.notificationservice.dto.EmailDetails;
import com.hoangtien2k3.notificationservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public Mono<String> sendSimpleMail(EmailDetails details) {
        return Mono.fromCallable(() -> {
                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setFrom(fromEmail);
                    mailMessage.setTo(details.getRecipient());
                    mailMessage.setText(details.getMsgBody());
                    mailMessage.setSubject(details.getSubject());

                    javaMailSender.send(mailMessage);
                    return "Mail Sent Successfully";
                })
                .onErrorResume(ex -> {
                    log.error("Error while sending mail", ex);
                    return Mono.just("Error while Sending Mail");
                });
    }

    @Override
    public Mono<String> sendMailWithAttachment(EmailDetails details) {
        return Mono.fromCallable(() -> {
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

                    mimeMessageHelper.setFrom(fromEmail);
                    mimeMessageHelper.setTo(details.getRecipient());
                    mimeMessageHelper.setText(details.getMsgBody());
                    mimeMessageHelper.setSubject(details.getSubject());

                    FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
                    mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

                    javaMailSender.send(mimeMessage);
                    return "Mail Sent Successfully";
                })
                .onErrorResume(MessagingException.class, ex -> {
                    log.error("Error while sending mail with attachment", ex);
                    return Mono.just("Error while Sending Mail with Attachment");
                });
    }

    @Override
    public Mono<String> sendMail(MultipartFile[] files, String to, String[] cc, String subject, String body) {
        return Mono.fromCallable(() -> {
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                    mimeMessageHelper.setFrom(fromEmail);
                    mimeMessageHelper.setTo(to);
                    mimeMessageHelper.setCc(cc);
                    mimeMessageHelper.setSubject(subject);
                    mimeMessageHelper.setText(body);

                    for (MultipartFile file : files) {
                        mimeMessageHelper.addAttachment(
                                Objects.requireNonNull(file.getOriginalFilename()),
                                new ByteArrayResource(file.getBytes()));
                    }

                    javaMailSender.send(mimeMessage);
                    return "Email sent successfully to " + Arrays.toString(cc);
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error while sending email", ex);
                    return Mono.just("Error while Sending Email");
                });
    }

}
