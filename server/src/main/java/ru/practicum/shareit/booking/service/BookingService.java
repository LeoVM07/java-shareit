package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingRequestDto requestDto, long userId);

    BookingDto updateBooking(long bookingId, long ownerId, boolean approved);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByUserId(long userId, String state);

    List<BookingDto> getAllBookingsByOwner(long ownerId, String state);

}
