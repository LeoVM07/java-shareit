package ru.practicum.shareit.booking.mapper;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Data
public class BookingMapper {

    public static BookingDto toDtoFromBooking(Booking booking) {
        BookingDto bookingDto = new BookingDto(
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toDtoFromUser(booking.getBooker())
        );
        bookingDto.setId(booking.getId());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static BookingInfoDto toInfoDtoFromBooking(Booking booking) {
        return new BookingInfoDto(booking.getId(), booking.getStart(), booking.getEnd());
    }
}
