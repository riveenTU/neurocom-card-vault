package com.neurocom.cardvault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No Credit Card details")
public class NoCardDetialsException extends RuntimeException{
    public NoCardDetialsException() {
        super("Non of the required card details present");
    }
    public NoCardDetialsException(String missingField) {
        super(missingField + " is required in the card details");
    }
}
