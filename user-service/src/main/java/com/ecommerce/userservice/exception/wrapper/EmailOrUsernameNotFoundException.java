package com.ecommerce.userservice.exception.wrapper;

public class EmailOrUsernameNotFoundException extends RuntimeException {
    public EmailOrUsernameNotFoundException() {
        super();
    }

    public EmailOrUsernameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailOrUsernameNotFoundException(String message) {
        super(message);
    }

    public EmailOrUsernameNotFoundException(Throwable cause) {
        super(cause);
    }
}
