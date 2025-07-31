package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "Необходимо указать имя пользователя")
    private final String name;
    @NotBlank(message = "Электронный адрес необходимо заполнить")
    @Email(message = "Некорректно заполненный электронный адрес")
    private final String email;
    private long id;
}
