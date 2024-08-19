package ru.practicum.exception;

public class InvalidTimeParamException extends RuntimeException {
    public InvalidTimeParamException(String msg) {
        super(msg);
    }
}