package com.platform.energy.services.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    private static final int serialVersionID = 1;

    public ConflictException(String message) {
        super(message);
    }
}
