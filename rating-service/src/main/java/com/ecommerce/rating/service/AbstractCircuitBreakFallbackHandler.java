package com.ecommerce.rating.service;

abstract class AbstractCircuitBreakFallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractCircuitBreakFallbackHandler.class);

    protected void handleBodilessFallback(Throwable throwable) throws Throwable {
        handleError(throwable);
    }

    protected Object handleFallback(Throwable throwable) throws Throwable {
        handleError(throwable);
        return null;
    }

    private void handleError(Throwable throwable) throws Throwable {
        log.error("Circuit breaker records an error. Detail {}", throwable.getMessage());
        throw throwable;
    }
}
