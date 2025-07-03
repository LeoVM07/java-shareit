package ru.practicum.shareit.user.dal.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface InMemoryUserStorage {

    List<User> showAllUser();

    UserDto addUser(UserDto user);

    UserDto showUser(long userId);

    UserDto updateUser(long userId, UserUpdateDto user);

    void deleteUser(long userId);
}
