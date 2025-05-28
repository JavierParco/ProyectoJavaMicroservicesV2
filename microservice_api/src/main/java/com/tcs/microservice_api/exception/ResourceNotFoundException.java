package com.tcs.microservice_api.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BaseCustomException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
