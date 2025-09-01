package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;


}
