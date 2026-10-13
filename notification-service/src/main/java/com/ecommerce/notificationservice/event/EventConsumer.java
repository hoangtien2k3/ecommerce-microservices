package com.ecommerce.notificationservice.event;

import com.ecommerce.notificationservice.constant.KafkaConstant;
import com.ecommerce.notificationservice.dto.PaymentDto;
import com.ecommerce.notificationservice.service.PaymentService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    private final PaymentService paymentService;
    private final EventProducer eventProducer;
    private final Gson gson = new Gson();

    @KafkaListener(topics = KafkaConstant.PROFILE_ONBOARDING_TOPIC,
            groupId = "${payment.kafka.consumer-group-id}")
    public void sendEmailKafkaOnboarding(String message) {
        log.info("AUTH-SERVICE Onboarding event received on notification service: {}", message);
    }

    @KafkaListener(topics = KafkaConstant.STATUS_PAYMENT_SUCCESSFUL,
            groupId = "${payment.kafka.consumer-group-id}")
    public void paymentOrderKafkaOnboarding(String message) {
        log.info("Payment event received on notification-service");
        PaymentDto paymentDto = gson.fromJson(message, PaymentDto.class);
        paymentService.savePayment(paymentDto);
        eventProducer.send(KafkaConstant.PROFILE_ONBOARDED_TOPIC, gson.toJson(paymentDto));
    }

}
