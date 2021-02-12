package com.dacaspex.provider.exception;

import com.dacaspex.exception.InvalidSchemaException;

public class NoSuchProviderException extends InvalidSchemaException {
    public NoSuchProviderException(String provider) {
        super(String.format("Unknown provider: %s", provider));
    }
}
