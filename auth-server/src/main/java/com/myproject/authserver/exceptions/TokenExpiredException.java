package com.myproject.authserver.exceptions;

import lombok.Generated;

@Generated
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}