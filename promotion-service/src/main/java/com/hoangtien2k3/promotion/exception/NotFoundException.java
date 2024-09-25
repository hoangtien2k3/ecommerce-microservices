package com.hoangtien2k3.promotion.exception;

import com.hoangtien2k3.promotion.utils.MessagesUtils;

public class NotFoundException extends RuntimeException {

    private String message;

    public NotFoundException(String errorCode, Object... var2) {
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
