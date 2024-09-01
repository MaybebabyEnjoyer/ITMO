package ru.itmo.wp.model.exception;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
