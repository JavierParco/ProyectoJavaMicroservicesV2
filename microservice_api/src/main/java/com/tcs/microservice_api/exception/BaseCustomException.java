package com.tcs.microservice_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Excepción base para la aplicación
class BaseCustomException extends RuntimeException {
    public BaseCustomException(String message) {
        super(message);
    }

    public BaseCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}


