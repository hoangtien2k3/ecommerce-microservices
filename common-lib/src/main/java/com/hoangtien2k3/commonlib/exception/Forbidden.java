package com.hoangtien2k3.commonlib.exception;

import com.hoangtien2k3.commonlib.utils.MessagesUtils;
import lombok.Setter;

@Setter
public class Forbidden extends RuntimeException {
    private String message;

    public Forbidden(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
