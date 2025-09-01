package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlankItemRequestDto {

    @NotBlank(message = "Необходимо заполнить описание вещи")
    private String description;
}
