package com.hoangtien2k3.proxyclient.exception.wrapper;

import java.io.Serial;

public class CredentialNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CredentialNotFoundException() {
        super();
    }

    public CredentialNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialNotFoundException(String message) {
        super(message);
    }

    public CredentialNotFoundException(Throwable cause) {
        super(cause);
    }

}