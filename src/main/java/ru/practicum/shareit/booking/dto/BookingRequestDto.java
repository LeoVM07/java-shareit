package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {

    @NotNull
    @FutureOrPresent
    private final LocalDateTime start;

    @NotNull
    @Future
    private final LocalDateTime end;

    @NotNull
    private long itemId;
}
