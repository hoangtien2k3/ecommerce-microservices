package com.ecommerce.commonlib.kafka.cdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Template-method base class for CDC (Change Data Capture) Kafka consumers.
 *
 * <p>Two flavors of {@code processMessage}:</p>
 * <ul>
 *   <li>Value-only: when the key is not relevant to the handler logic.</li>
 *   <li>Key + value: when the consumer needs to dedupe or partition by key.</li>
 * </ul>
 *
 * @param <K> key type
 * @param <V> value type
 */
public abstract class BaseCdcConsumer<K, V> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected void processMessage(V record, MessageHeaders headers, Consumer<V> consumer) {
        Object key = headers.get(KafkaHeaders.RECEIVED_KEY);
        if (log.isDebugEnabled()) {
            log.debug("CDC received headers={}", headers);
            log.debug("CDC processing key={} value={}", key, record);
        }
        consumer.accept(record);
        if (log.isDebugEnabled()) {
            log.debug("CDC processed key={}", key);
        }
    }

    protected void processMessage(K key, V value, MessageHeaders headers, BiConsumer<K, V> consumer) {
        if (log.isDebugEnabled()) {
            log.debug("CDC received headers={}", headers);
            log.debug("CDC processing key={} value={}", key, value);
        }
        consumer.accept(key, value);
        if (log.isDebugEnabled()) {
            log.debug("CDC processed key={}", key);
        }
    }
}
