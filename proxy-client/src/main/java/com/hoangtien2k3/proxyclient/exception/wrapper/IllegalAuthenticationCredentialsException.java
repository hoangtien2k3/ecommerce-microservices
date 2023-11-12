package com.hoangtien2k3.proxyclient.exception.wrapper;

public class IllegalAuthenticationCredentialsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalAuthenticationCredentialsException() {
        super();
    }

    public IllegalAuthenticationCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalAuthenticationCredentialsException(String message) {
        super(message);
    }

    public IllegalAuthenticationCredentialsException(Throwable cause) {
        super(cause);
    }

}