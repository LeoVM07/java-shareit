package ru.practicum.shareit.exception;

public class BookingIdException extends RuntimeException {
    public BookingIdException(long bookingId) {
        super(String.format("Бронирование с id %d не найдено", bookingId));
    }
}
