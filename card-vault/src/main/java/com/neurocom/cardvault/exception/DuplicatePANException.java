package com.neurocom.cardvault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Duplicate PAN")
public class DuplicatePANException  extends RuntimeException {
    public DuplicatePANException() {
        super("PAN already exists");
    }

}
