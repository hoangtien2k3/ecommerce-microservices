package com.hoangtien2k3qx1.favouriteservice.exception.wrapper;

import java.io.Serial;

public class FavouriteNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public FavouriteNotFoundException() {
        super();
    }

    public FavouriteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FavouriteNotFoundException(String message) {
        super(message);
    }

    public FavouriteNotFoundException(Throwable cause) {
        super(cause);
    }
}
