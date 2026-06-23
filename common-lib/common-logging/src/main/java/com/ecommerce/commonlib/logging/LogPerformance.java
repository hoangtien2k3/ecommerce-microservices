package com.ecommerce.commonlib.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method (or all methods in a class) for performance logging.
 *
 * <p>The AOP aspect logs duration and result only when execution time exceeds
 * {@code ecommerce.web.logging.performance.threshold-ms} (default 50ms),
 * keeping logs quiet for fast, healthy calls.
 *
 * <pre>{@code
 * @LogPerformance(title = "fetch product", logInput = true)
 * public ProductDto getById(Integer id) { ... }
 * }</pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogPerformance {

    /** Human-readable label shown in the log line. Defaults to class.method. */
    String title() default "";

    /** Log method arguments. Off by default to avoid logging sensitive data. */
    boolean logInput() default false;

    /** Log method return value. Off by default to avoid logging large payloads. */
    boolean logOutput() default false;
}
