package com.tcs.microservice_api.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // O UNPROCESSABLE_ENTITY (422)
public class SaldoInsuficienteException extends BaseCustomException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}