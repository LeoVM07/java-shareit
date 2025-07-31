package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

@Data
public class ItemBookedDto {

    @NotBlank(message = "Необходимо указать название вещи")
    private final String name;
    @NotBlank(message = "Необходимо оставить описание вещи")
    @Size(max = 200, message = "Описание не должно быть длиннее 200 символов")
    private final String description;
    @NotNull(message = "Необходимо указать доступность вещи")
    private final Boolean available;
    private long id;
    private long ownerId;

    private BookingInfoDto lastBooking;

    private BookingInfoDto nextBooking;

    private List<CommentDto> comments;

}
