package com.techelevator.tenmo.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Insufficient Funds.")
public class InsufficientFundsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InsufficientFundsException() {
        super("Insufficient Funds.");
    }
	
}
