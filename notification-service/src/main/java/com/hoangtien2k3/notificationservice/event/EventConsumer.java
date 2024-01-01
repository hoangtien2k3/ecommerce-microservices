package com.hoangtien2k3.notificationservice.event;

import com.google.gson.Gson;
import com.hoangtien2k3.notificationservice.config.KafkaConstant;
import com.hoangtien2k3.notificationservice.dto.EmailDetails;
import com.hoangtien2k3.notificationservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;

@Service
@Slf4j
public class EventConsumer {
    Gson gson = new Gson(); // convert Json -> DTO

    @Autowired
    private EmailService emailService;

    @Autowired
    EventProducer eventProducer;

    public EventConsumer(ReceiverOptions<String, String> receiverOptions) {
        KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(KafkaConstant.PROFILE_ONBOARDING_TOPIC)))
                .receive()
                .subscribe(this::sendEmailKafkaOnboarding);
    }

    public void sendEmailKafkaOnboarding(ReceiverRecord<String, String> receiverRecord) {
        log.info("USER-SERVICE Onboarding event send email on notification service.");
        EmailDetails emailDetails = gson.fromJson(receiverRecord.value(), EmailDetails.class);

        emailService.sendSimpleMail(emailDetails).subscribe(email -> {
            log.info("send email successfully -> user-service change password.");
            eventProducer.send(KafkaConstant.PROFILE_ONBOARDED_TOPIC, gson.toJson(emailDetails)).subscribe();
        });
    }
}