package ru.practicum.shareit.exception;

public class ItemAvailabilityException extends RuntimeException {
    public ItemAvailabilityException(long itemId) {
        super(String.format("Вещь с id %d не доступна для бронирования", itemId));
    }
}
