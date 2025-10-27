package com.neurocom.cardvault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid PAN")
public class InvalidPANException extends RuntimeException {
    public InvalidPANException() {
        super("PAN is invalid");
    }
}
