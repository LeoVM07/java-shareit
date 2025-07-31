package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Необходимо указать имя пользователя")
    private String name;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "Электронный адрес необходимо заполнить")
    @Email(message = "Некорректно заполненный электронный адрес")
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
