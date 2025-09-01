package ru.practicum.shareit.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;


@Data
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        log.info("Создание пользователя {}", user);
        return userClient.createUser(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive @NotNull @PathVariable("userId") Long userId) {
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Positive @NotNull @PathVariable("userId") long userId,
                                             @Valid @RequestBody UserUpdateDto user) {
        return userClient.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @NotNull @PathVariable("userId") long userId) {
        return userClient.deleteUser(userId);
    }

}
