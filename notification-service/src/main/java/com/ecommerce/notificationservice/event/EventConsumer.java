package com.ecommerce.notificationservice.event;

import com.ecommerce.notificationservice.constant.KafkaConstant;
import com.ecommerce.notificationservice.dto.EmailDetails;
import com.ecommerce.notificationservice.dto.PaymentDto;
import com.ecommerce.notificationservice.entity.PaymentStatus;
import com.ecommerce.notificationservice.service.EmailService;
import com.ecommerce.notificationservice.service.PaymentService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    private final EmailService emailService;
    private final PaymentService paymentService;
    private final EventProducer eventProducer;
    private final Gson gson = new Gson();

    @KafkaListener(topics = KafkaConstant.PROFILE_ONBOARDING_TOPIC,
            groupId = "${payment.kafka.consumer-group-id}")
    public void sendEmailKafkaOnboarding(String message) {
        log.info("AUTH-SERVICE Onboarding event: send email on notification service");
        EmailDetails emailDetails = gson.fromJson(message, EmailDetails.class);
        String result = emailService.sendSimpleMail(emailDetails);
        log.info("Email sent: {}", result);
        eventProducer.send(KafkaConstant.PROFILE_ONBOARDED_TOPIC, gson.toJson(result));
    }

    @KafkaListener(topics = KafkaConstant.STATUS_PAYMENT_SUCCESSFUL,
            groupId = "${payment.kafka.consumer-group-id}")
    public void paymentOrderKafkaOnboarding(String message) {
        log.info("Payment event received on notification-service");
        PaymentDto paymentDto = gson.fromJson(message, PaymentDto.class);
        paymentService.savePayment(paymentDto);
        eventProducer.send(KafkaConstant.PROFILE_ONBOARDED_TOPIC, gson.toJson(paymentDto));

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient("hoangtien2k3dev@gmail.com")
                .msgBody(msgBody(paymentDto.getIsPayed(), paymentDto.getPaymentStatus()))
                .subject("Payment Successfully in Order with userId: " + paymentDto.getUserId())
                .attachment("Please, check the full information in invoice: " + LocalDateTime.now())
                .build();
        String emailResult = emailService.sendSimpleMail(emailDetails);
        eventProducer.send(KafkaConstant.PROFILE_ONBOARDED_TOPIC, gson.toJson(emailResult));
    }

    private String msgBody(Boolean isPayed, PaymentStatus paymentStatus) {
        return "Payment in order product cart successfully: \n " +
                " + IsPays: " + isPayed +
                "\n + PaymentStatus: " + paymentStatus.getStatus() +
                "\n\nDate: " + LocalDate.now() +
                "\nTime: " + LocalTime.now();
    }
}
