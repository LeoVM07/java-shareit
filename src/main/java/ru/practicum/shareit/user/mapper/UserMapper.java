package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto toDtoFromUser(User user) {
        UserDto userDto = new UserDto(user.getName(), user.getEmail());
        userDto.setId(user.getId());
        return userDto;
    }

    public static User toUserFromDto(UserDto userDto) {
        User user = new User(userDto.getName(), userDto.getEmail());
        user.setId(userDto.getId());
        return user;
    }

    public static User toUserFromUpdate(UserUpdateDto user) {
        return new User(user.getName(), user.getEmail());
    }

}
