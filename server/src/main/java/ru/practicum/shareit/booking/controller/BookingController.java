package ru.practicum.shareit.booking.controller;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Data
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingRequestDto requestDto,
                                                    @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return new ResponseEntity<>(bookingService.createBooking(requestDto, bookerId), HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable("bookingId") long bookingId,
                                                    @RequestHeader("X-Sharer-User-Id") long ownerId,
                                                    @RequestParam("approved") boolean approved) {
        return new ResponseEntity<>(bookingService.updateBooking(bookingId, ownerId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable("bookingId") long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return new ResponseEntity<>(bookingService.getBookingById(bookingId, userId), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookingsByUserId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return new ResponseEntity<>(bookingService.getAllBookingsByUserId(userId, state), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return new ResponseEntity<>(bookingService.getAllBookingsByOwner(ownerId, state), HttpStatus.OK);
    }

}
