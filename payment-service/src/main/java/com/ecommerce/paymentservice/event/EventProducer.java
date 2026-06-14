package com.ecommerce.paymentservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EventProducer {

    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send Kafka message to topic {}: {}", topic, ex.getMessage());
                    } else {
                        log.debug("Sent message to topic {} offset {}", topic, result.getRecordMetadata().offset());
                    }
                });
    }
}
