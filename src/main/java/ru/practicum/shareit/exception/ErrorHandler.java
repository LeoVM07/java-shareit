package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserIdException(final UserIdException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка в id пользователя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserEmailException(final UserEmailException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка в email пользователя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemIdException(final ItemIdException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка в id вещи", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemAvailabilityException(final ItemAvailabilityException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка в доступности вещи", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateIdException(final DuplicateIdException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка двойного ввода данных", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingIdException(final BookingIdException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка в id бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка валидации данных", e.getMessage());
    }

    @Getter
    public static class ErrorResponse {
        private final String error;
        private final String description;

        private ErrorResponse(String error, String description) {
            this.error = error;
            this.description = description;
        }
    }
}
