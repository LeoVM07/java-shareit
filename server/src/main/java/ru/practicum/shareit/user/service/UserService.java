package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {
    UserDto getUserById(long userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserUpdateDto userUpdateDto);

    void deleteUser(long userId);
}
