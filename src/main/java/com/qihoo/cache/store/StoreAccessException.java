package com.qihoo.cache.store;

/**
 * Created by zhuhailong-dc on 2018/3/29.
 */
public class StoreAccessException extends Exception {

    private static final long serialVersionUID = -3962141124378773527L;

    /**
     * Creates a new exception wrapping the {@link Throwable cause} passed in.
     */
    public StoreAccessException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception wrapping the {@link Throwable cause} passed in and with the provided message.
     */
    public StoreAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception with the provided message
     */
    public StoreAccessException(String message) {
        super(message);
    }


}
