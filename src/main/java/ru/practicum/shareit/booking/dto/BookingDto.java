package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    @NotNull
    private final LocalDateTime start;
    @NotNull
    private final LocalDateTime end;
    @NotNull
    private final ItemDto item;
    @NotNull
    private final UserDto booker;
    private long id;
    @NotNull
    private BookingStatus status;

}
