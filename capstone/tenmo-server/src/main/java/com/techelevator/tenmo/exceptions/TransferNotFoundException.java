package com.techelevator.tenmo.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransferNotFoundException extends Exception {
    public TransferNotFoundException(String msg) {
        super(msg);
    }
}
