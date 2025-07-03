package ru.practicum.shareit.user.dal;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserEmailException;
import ru.practicum.shareit.exception.UserIdException;
import ru.practicum.shareit.user.dal.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class InMemoryUserStorageImpl implements InMemoryUserStorage {


    private final Map<Long, User> allUsers = new HashMap<>();
    private long userId = 1;


    @Override
    public List<User> showAllUser() {
        log.trace("Выведен список пользователей");
        return allUsers.values().stream().toList();
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User userToAdd = UserMapper.toUserFromDto(userDto);
        checkExistingEmail(userToAdd);

        userToAdd.setId(generateId());
        userDto.setId(userToAdd.getId());

        allUsers.put(userToAdd.getId(), userToAdd);
        log.info("Пользователь с id {} был добавлен", userToAdd.getId());
        return userDto;
    }

    @Override
    public UserDto showUser(long userId) {
        checkUserId(userId);
        log.trace("Пользователь с id {} был выведен на экран", userId);
        return UserMapper.toDtoFromUser(allUsers.get(userId));
    }

    @Override
    public UserDto updateUser(long userId, UserUpdateDto userUpdate) {
        checkUserId(userId);
        User userToUpdate = allUsers.get(userId);
        User newUser = UserMapper.toUserFromUpdate(userUpdate);

        String newName = newUser.getName();
        String newEmail = newUser.getEmail();

        if (newName != null && !newName.isBlank()) {
            userToUpdate.setName(newName);
        }
        if (newEmail != null && !newEmail.isBlank()) {
            checkExistingEmail(newUser);
            userToUpdate.setEmail(newEmail);
        }

        log.info("Пользователь с id {} был обновлён", userId);
        return UserMapper.toDtoFromUser(userToUpdate);
    }

    @Override
    public void deleteUser(long userId) {
        checkUserId(userId);
        allUsers.remove(userId);
        log.info("Пользователь с id {} был удалён", userId);
    }

    private long generateId() {
        return userId++;
    }

    private void checkExistingEmail(User userToCheck) {
        for (User u : allUsers.values()) {
            if (userToCheck.getEmail().equals(u.getEmail()) && userToCheck.getId() != u.getId()) {
                throw new UserEmailException(u.getEmail());
            }
        }
    }

    private void checkUserId(long userId) {
        if (!allUsers.containsKey(userId)) {
            throw new UserIdException(userId);
        }
    }

}
