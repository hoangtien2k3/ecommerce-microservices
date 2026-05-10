package com.ecommerce.paymentservice.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveKafkaAppProperties {
    @Value("${kafka.bootstrap.servers}")
    String bootstrapServers;

    @Value("${payment.kafka.consumer-group-id}")
    String consumerGroupId;
}
