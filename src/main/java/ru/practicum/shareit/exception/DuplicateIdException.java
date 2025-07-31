package ru.practicum.shareit.exception;

public class DuplicateIdException extends RuntimeException {
    public DuplicateIdException(String message) {
        super(message);
    }
}
