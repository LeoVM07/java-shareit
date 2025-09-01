package ru.practicum.shareit.booking.enums;

import lombok.Getter;

@Getter
public enum BookingStatus {
    WAITING("waiting"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELED("canceled");

    private final String bookingStatus;

    BookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
