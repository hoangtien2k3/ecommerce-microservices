package com.ecommerce.commonlib.kafka.cdc.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

/**
 * Base class for a typed Kafka listener container factory.
 *
 * <p>Subclasses pin {@code K}/{@code V} and expose {@link #listenerContainerFactory()} as a
 * Spring bean so the container can be referenced by name from {@code @KafkaListener}.</p>
 *
 * <h3>Why wrap with {@link ErrorHandlingDeserializer}?</h3>
 * Poison records would otherwise tombstone the partition. Wrapping converts deserialization
 * failures into a {@code DeserializationException} that Spring Kafka can route to a
 * dead-letter topic without losing position.
 */
public abstract class BaseKafkaListenerConfig<K, V> {

    private final Class<K> keyType;
    private final Class<V> valueType;
    private final KafkaProperties kafkaProperties;

    protected BaseKafkaListenerConfig(Class<K> keyType, Class<V> valueType, KafkaProperties kafkaProperties) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.kafkaProperties = kafkaProperties;
    }

    public abstract ConcurrentKafkaListenerContainerFactory<K, V> listenerContainerFactory();

    protected ConcurrentKafkaListenerContainerFactory<K, V> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<K, V>();
        factory.setConsumerFactory(typedConsumerFactory());
        return factory;
    }

    private ConsumerFactory<K, V> typedConsumerFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new ErrorHandlingDeserializer<>(jsonDeserializer(keyType)),
                new ErrorHandlingDeserializer<>(jsonDeserializer(valueType))
        );
    }

    private static <T> JsonDeserializer<T> jsonDeserializer(Class<T> clazz) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz);
        deserializer.addTrustedPackages("*");
        return deserializer;
    }
}
