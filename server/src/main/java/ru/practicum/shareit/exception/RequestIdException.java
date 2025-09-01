package ru.practicum.shareit.exception;

public class RequestIdException extends RuntimeException {
    public RequestIdException(long requestId) {
        super(String.format("Запрос с id %d не найден", requestId));
    }
}
