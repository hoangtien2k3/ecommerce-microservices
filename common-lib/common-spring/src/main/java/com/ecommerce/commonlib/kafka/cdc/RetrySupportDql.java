package com.ecommerce.commonlib.kafka.cdc;

import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.retry.annotation.Backoff;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composes {@link RetryableTopic} with the platform defaults: 6-second backoff, 4 attempts,
 * auto-created topics, single retry topic per interval, and a Dead-Letter Topic sink.
 *
 * <p>Each field uses {@link AliasFor} so attribute overrides written by the consumer are
 * forwarded to the underlying {@link RetryableTopic} as if they had been declared directly.</p>
 */
@Documented
@RetryableTopic
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetrySupportDql {

    @AliasFor(annotation = RetryableTopic.class, attribute = "backoff")
    Backoff backoff() default @Backoff(value = 6000);

    @AliasFor(annotation = RetryableTopic.class, attribute = "attempts")
    String attempts() default "4";

    @AliasFor(annotation = RetryableTopic.class, attribute = "autoCreateTopics")
    String autoCreateTopics() default "true";

    @AliasFor(annotation = RetryableTopic.class, attribute = "listenerContainerFactory")
    String listenerContainerFactory() default "";

    @AliasFor(annotation = RetryableTopic.class, attribute = "exclude")
    Class<? extends Throwable>[] exclude() default {};

    @AliasFor(annotation = RetryableTopic.class, attribute = "sameIntervalTopicReuseStrategy")
    SameIntervalTopicReuseStrategy sameIntervalTopicReuseStrategy() default SameIntervalTopicReuseStrategy.SINGLE_TOPIC;
}
