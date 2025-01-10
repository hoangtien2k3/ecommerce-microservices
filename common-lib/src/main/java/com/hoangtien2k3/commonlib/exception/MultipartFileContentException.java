package com.hoangtien2k3.commonlib.exception;

public class MultipartFileContentException extends RuntimeException {

    public MultipartFileContentException() {
        super();
    }

    public MultipartFileContentException(String errorMessage) {
        super(errorMessage);
    }

    public MultipartFileContentException(Throwable cause) {
        super(cause);
    }

    public MultipartFileContentException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
