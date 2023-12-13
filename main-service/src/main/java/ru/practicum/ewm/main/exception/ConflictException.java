package ru.practicum.ewm.main.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(final String message) {
        super(message);
    }
}
