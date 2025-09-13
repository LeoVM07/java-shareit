package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDto {

    @NotBlank(message = "Необходимо указать имя пользователя")
    @Length(max = 20, message = "Длина имени пользователя не может превышать 20 символов")
    private final String name;
    @NotBlank(message = "Электронный адрес необходимо заполнить")
    @Email(message = "Некорректно заполненный электронный адрес")
    private final String email;
    private Long id;
}
