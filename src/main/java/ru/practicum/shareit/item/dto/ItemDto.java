package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemDto {

    private long id;

    @NotBlank(message = "Необходимо указать название вещи")
    private final String name;

    @NotBlank(message = "Необходимо оставить описание вещи")
    @Size(max = 200, message = "Описание не должно быть длиннее 200 символов")
    private final String description;

    @NotNull(message = "Необходимо указать доступность вещи")
    private final Boolean available;

    private long ownerId;

}
