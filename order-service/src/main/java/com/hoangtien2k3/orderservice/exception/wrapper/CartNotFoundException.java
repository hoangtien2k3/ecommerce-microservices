package com.hoangtien2k3.orderservice.exception.wrapper;

import java.io.Serial;

public class CartNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public CartNotFoundException() {
        super();
    }

    public CartNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(Throwable cause) {
        super(cause);
    }
}
