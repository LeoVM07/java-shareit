package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    @NotBlank(message = "Необходимо указать название вещи")
    private String name;

    @NotBlank(message = "Необходимо оставить описание вещи")
    @Size(max = 200, message = "Описание не должно быть длиннее 200 символов")
    private String description;

    @NotNull(message = "Необходимо указать доступность вещи")
    private Boolean available;

    private Long requestId;

}
