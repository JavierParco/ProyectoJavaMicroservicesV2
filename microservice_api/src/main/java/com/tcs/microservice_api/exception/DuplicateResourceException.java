package com.tcs.microservice_api.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // O CONFLICT (409)
public class DuplicateResourceException extends BaseCustomException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
