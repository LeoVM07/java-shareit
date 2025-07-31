package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemUpdateDto {

    private String name;

    @Size(max = 200, message = "Описание не должно быть длиннее 200 символов")
    private String description;

    private Boolean available;

    // private long requestId;
}
