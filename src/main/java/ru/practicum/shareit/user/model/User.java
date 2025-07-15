package ru.practicum.shareit.user.model;

import lombok.Data;
import jakarta.validation.constraints.*;


@Data
public class User {

    private long id;

    @NotBlank(message = "Необходимо указать имя пользователя")
    private String name;

    @NotBlank(message = "Электронный адрес необходимо заполнить")
    @Email(message = "Некорректно заполненный электронный адрес")
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
