package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ViolationException extends RuntimeException {
    public ViolationException(String message) {
        super(message);
    }
}
