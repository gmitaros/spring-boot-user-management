package com.myproject.authserver.exceptions;

import lombok.Generated;

@Generated
public class TokenAlreadyValidatedException extends RuntimeException {
    public TokenAlreadyValidatedException(String message) {
        super(message);
    }
}