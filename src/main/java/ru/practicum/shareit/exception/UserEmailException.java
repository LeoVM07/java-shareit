package ru.practicum.shareit.exception;

public class UserEmailException extends RuntimeException {
    public UserEmailException(String userEmail) {
        super("Email " + userEmail + " уже используется другим пользователем!");
    }
}
