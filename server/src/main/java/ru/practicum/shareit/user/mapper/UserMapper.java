package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto toDtoFromUser(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUserFromDto(UserDto userDto) {
        User user = new User(userDto.getName(), userDto.getEmail());
        user.setId(userDto.getId());
        return user;
    }
}
