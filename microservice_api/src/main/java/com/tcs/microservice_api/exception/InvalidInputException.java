package com.tcs.microservice_api.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInputException extends BaseCustomException {
    public InvalidInputException(String message) {
        super(message);
    }
}