package com.example.demoelk.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class SearchValidationException extends RuntimeException {

    public SearchValidationException(String message) {
        super(message);
    }
}
