package ru.practicum.shareit.user.service;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dal.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage userStorage;

    public List<User> showAllUsers() {
        return userStorage.showAllUser();
    }

    public UserDto addUser(@Valid UserDto user) {
        return userStorage.addUser(user);
    }

    public UserDto showUser(long userId) {
        return userStorage.showUser(userId);
    }

    public UserDto updateUser(long userId, UserUpdateDto user) {
        return userStorage.updateUser(userId, user);
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }
}
