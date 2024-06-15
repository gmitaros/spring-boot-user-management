package com.myproject.authserver.exceptions;

import com.myproject.authserver.dto.enums.BusinessErrorCodes;
import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Generated
public class GenericException extends RuntimeException {

    private HttpStatusCode httpStatusCode;
    private int code;
    private String description;

    public GenericException(String message) {
        super(message);
    }

    public GenericException(int code, HttpStatus status, String description) {
        super(description);
        this.code = code;
        this.httpStatusCode = status;
        this.description = description;
    }

    public GenericException(BusinessErrorCodes error) {
        super(error.getDescription());
        this.code = error.getCode();
        this.httpStatusCode = error.getHttpStatus();
        this.description = error.getDescription();
    }
}