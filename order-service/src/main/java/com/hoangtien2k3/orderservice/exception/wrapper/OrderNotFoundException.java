package com.hoangtien2k3.orderservice.exception.wrapper;

import java.io.Serial;

public class OrderNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public OrderNotFoundException() {
        super();
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }
}
