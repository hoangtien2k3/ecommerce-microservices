package com.hoangtien2k3.proxyclient.exception.wrapper;

public class VerificationTokenNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VerificationTokenNotFoundException() {
        super();
    }

    public VerificationTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationTokenNotFoundException(String message) {
        super(message);
    }

    public VerificationTokenNotFoundException(Throwable cause) {
        super(cause);
    }

}