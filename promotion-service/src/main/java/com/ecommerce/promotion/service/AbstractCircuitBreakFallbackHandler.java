package com.ecommerce.promotion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

abstract class AbstractCircuitBreakFallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractCircuitBreakFallbackHandler.class);

    protected void handleBodilessFallback(List<Long> ids, Throwable throwable) throws Throwable {
        handleError(ids, throwable);
    }

    protected Object handleFallback(List<Long> ids, Throwable throwable) throws Throwable {
        handleError(ids, throwable);
        return null;
    }

    private void handleError(List<Long> ids, Throwable throwable) throws Throwable {
        log.error("Circuit breaker records an error. Detail {} with ids {}", throwable.getMessage(), ids);
        throw throwable;
    }
}
