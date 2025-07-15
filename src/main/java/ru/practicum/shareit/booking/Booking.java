package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;

@Data
public class Booking {
    private long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private final User booker;
    private final BookingStatus status;

}
