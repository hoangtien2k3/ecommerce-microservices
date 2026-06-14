package com.ecommerce.notificationservice.service.impl;

import com.ecommerce.notificationservice.dto.EmailDetails;
import com.ecommerce.notificationservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public String sendSimpleMail(EmailDetails details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully";
        } catch (Exception ex) {
            log.error("Error while sending mail", ex);
            return "Error while Sending Mail";
        }
    }

    @Override
    public String sendMailWithAttachment(EmailDetails details) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmail);
            helper.setTo(details.getRecipient());
            helper.setText(details.getMsgBody());
            helper.setSubject(details.getSubject());
            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
            helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
            javaMailSender.send(mimeMessage);
            return "Mail Sent Successfully";
        } catch (MessagingException ex) {
            log.error("Error while sending mail with attachment", ex);
            return "Error while Sending Mail with Attachment";
        }
    }

    @Override
    public String sendMail(MultipartFile[] files, String to, String[] cc, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setCc(cc);
            helper.setSubject(subject);
            helper.setText(body);
            for (MultipartFile file : files) {
                helper.addAttachment(
                        Objects.requireNonNull(file.getOriginalFilename()),
                        new ByteArrayResource(file.getBytes()));
            }
            javaMailSender.send(mimeMessage);
            return "Email sent successfully to " + Arrays.toString(cc);
        } catch (Exception ex) {
            log.error("Error while sending email", ex);
            return "Error while Sending Email";
        }
    }
}
