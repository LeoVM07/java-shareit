package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private long id;

    @NotBlank(message = "Необходимо оставить описание запрашиваемой вещи")
    @Size(max = 200, message = "Описание не должно быть длиннее 200 символов")
    private final String description;

    private final User requestor;
    private final LocalDateTime created;
}
