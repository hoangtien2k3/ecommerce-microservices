package com.ecommerce.commonlib.web.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Configuration for HTTP request/response logging.
 *
 * <p>AOP performance logging ({@code ecommerce.web.logging.performance.*}) lives in the
 * {@code common-logging} module's {@code PerformanceLogProperties}.</p>
 *
 * <pre>{@code
 * ecommerce:
 *   web:
 *     logging:
 *       request:
 *         enabled: true
 *         include-body: true
 *         max-body-bytes: 2048
 *       response:
 *         enabled: true
 *         include-body: false
 *         max-body-bytes: 2048
 * }</pre>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ecommerce.web.logging")
public class HttpLogProperties {

    @NestedConfigurationProperty
    private Request request = new Request();

    @NestedConfigurationProperty
    private Response response = new Response();

    @Getter
    @Setter
    public static class Request {
        private boolean enabled = true;
        private boolean includeBody = true;
        private int maxBodyBytes = 2048;
    }

    @Getter
    @Setter
    public static class Response {
        private boolean enabled = true;
        /** Off by default — response bodies can be large; enable only when debugging. */
        private boolean includeBody = false;
        private int maxBodyBytes = 2048;
    }
}
