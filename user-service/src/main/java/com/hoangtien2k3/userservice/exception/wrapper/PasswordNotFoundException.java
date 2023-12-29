package com.hoangtien2k3.userservice.exception.wrapper;

public class PasswordNotFoundException extends RuntimeException {

    public PasswordNotFoundException() {
        super();
    }

    public PasswordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordNotFoundException(String message) {
        super(message);
    }

    public PasswordNotFoundException(Throwable cause) {
        super(cause);
    }
}
