package ru.practicum.shareit.user.service;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserEmailException;
import ru.practicum.shareit.exception.UserIdException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;


@Data
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserIdException(userId));
        return UserMapper.toDtoFromUser(user);
    }

    public UserDto createUser(@Valid UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserEmailException(userDto.getEmail());
        }

        User user = UserMapper.toUserFromDto(userDto);
        userRepository.save(user);
        return UserMapper.toDtoFromUser(user);
    }


    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdException(userId));

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userUpdateDto.getEmail())) {
                throw new UserEmailException(userUpdateDto.getEmail());
            }
            existingUser.setEmail(userUpdateDto.getEmail());
        }

        if (userUpdateDto.getName() != null) {
            existingUser.setName(userUpdateDto.getName());
        }

        return UserMapper.toDtoFromUser(existingUser);
    }

    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserIdException(userId);
        }
        userRepository.deleteById(userId);
    }
}
