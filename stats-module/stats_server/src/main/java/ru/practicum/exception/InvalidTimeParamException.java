package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTimeParamException extends RuntimeException {
    public InvalidTimeParamException(String msg) {
        super(msg);
    }
}