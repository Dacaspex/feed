package com.dacaspex.provider.tweakers.exception;

public class UnexpectedSchemaException extends Exception {
    public UnexpectedSchemaException(String message) {
        super(message);
    }

    public UnexpectedSchemaException(Throwable cause) {
        super(cause);
    }
}
