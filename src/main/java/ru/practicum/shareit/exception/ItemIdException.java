package ru.practicum.shareit.exception;

public class ItemIdException extends RuntimeException {
    public ItemIdException(long itemId) {
        super(String.format("Вещь с id %d не найдена!", itemId));
    }
}
