package com.hoangtien2k3.orderservice.exception.wrapper;

import java.io.Serial;

public class JwtAuthenticationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public JwtAuthenticationException() {
        super();
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtAuthenticationException(String message) {
        super(message);
    }

    public JwtAuthenticationException(Throwable cause) {
        super(cause);
    }
}
