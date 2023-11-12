package com.hoangtien2k3.proxyclient.exception.wrapper;

import java.io.Serial;

public class UserObjectNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserObjectNotFoundException() {
        super();
    }

    public UserObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserObjectNotFoundException(String message) {
        super(message);
    }

    public UserObjectNotFoundException(Throwable cause) {
        super(cause);
    }

}