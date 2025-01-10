package com.hoangtien2k3.commonlib.exception;

import com.hoangtien2k3.commonlib.utils.MessagesUtils;

public class Forbidden extends RuntimeException {
    private String message;

    public Forbidden(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
