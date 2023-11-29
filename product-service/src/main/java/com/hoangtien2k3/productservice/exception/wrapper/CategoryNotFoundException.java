package com.hoangtien2k3.productservice.exception.wrapper;

import java.io.Serial;

public class CategoryNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CategoryNotFoundException() {
        super();
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(Throwable cause) {
        super(cause);
    }

}
