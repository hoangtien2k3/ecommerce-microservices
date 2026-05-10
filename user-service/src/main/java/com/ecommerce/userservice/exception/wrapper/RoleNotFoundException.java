package com.ecommerce.userservice.exception.wrapper;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super();
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(Throwable cause) {
        super(cause);
    }
}
