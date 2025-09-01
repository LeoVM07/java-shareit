package ru.practicum.shareit.booking.mapper;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Data
public class BookingMapper {

    public static BookingDto toDtoFromBooking(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toDtoFromUser(booking.getBooker()),
                booking.getStatus()
        );
    }
}
