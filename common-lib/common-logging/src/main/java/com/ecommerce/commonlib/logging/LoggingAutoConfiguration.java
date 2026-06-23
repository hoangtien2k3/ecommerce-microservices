package com.ecommerce.commonlib.logging;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Registers the {@link LoggerAspect} that powers the {@link LogPerformance} and
 * {@link Loggable} annotations.
 *
 * <p>Previously wired inside {@code common-spring}'s {@code WebAutoConfiguration};
 * extracted here so the logging concern is self-contained. Activated whenever AspectJ
 * is on the classpath and {@code ecommerce.web.logging.performance.enabled} is not
 * {@code false}. A service can supply its own {@link LoggerAspect} bean to override.</p>
 */
@AutoConfiguration
@ConditionalOnClass(Aspect.class)
@ConditionalOnProperty(prefix = "ecommerce.web.logging.performance", name = "enabled",
        havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PerformanceLogProperties.class)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LoggerAspect.class)
    public LoggerAspect loggerAspect(PerformanceLogProperties props) {
        return new LoggerAspect(props);
    }
}
