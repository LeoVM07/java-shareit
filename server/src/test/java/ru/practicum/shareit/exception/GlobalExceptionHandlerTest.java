package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleUserIdException_ShouldReturnCorrectErrorResponse() {
        UserIdException exception = new UserIdException(1);
        ErrorHandler.ErrorResponse response = errorHandler.handleUserIdException(exception);

        assertNotNull(response);
        assertEquals("Ошибка в id пользователя", response.getError());
        assertEquals("Пользователь с id 1 не найден", response.getDescription());
    }

    @Test
    void handleUserEmailException_ShouldReturnCorrectErrorResponse() {
        UserEmailException exception = new UserEmailException("sapozhok@mail.com");
        ErrorHandler.ErrorResponse response = errorHandler.handleUserEmailException(exception);

        assertNotNull(response);
        assertEquals("Ошибка в email пользователя", response.getError());
        assertEquals("Email sapozhok@mail.com уже используется другим пользователем!", response.getDescription());
    }

    @Test
    void handleItemIdException_ShouldReturnCorrectErrorResponse() {
        ItemIdException exception = new ItemIdException(1);
        ErrorHandler.ErrorResponse response = errorHandler.handleItemIdException(exception);

        assertNotNull(response);
        assertEquals("Ошибка в id вещи", response.getError());
        assertEquals("Вещь с id 1 не найдена", response.getDescription());
    }

    @Test
    void handleItemAvailabilityException_ShouldReturnCorrectErrorResponse() {
        ItemAvailabilityException exception = new ItemAvailabilityException(1);
        ErrorHandler.ErrorResponse response = errorHandler.handleItemAvailabilityException(exception);

        assertNotNull(response);
        assertEquals("Ошибка в доступности вещи", response.getError());
        assertEquals("Вещь с id 1 не доступна для бронирования", response.getDescription());
    }

    @Test
    void handleDuplicateIdException_ShouldReturnCorrectErrorResponse() {
        DuplicateIdException exception = new DuplicateIdException("Двойной ввод данных");
        ErrorHandler.ErrorResponse response = errorHandler.handleDuplicateIdException(exception);

        assertNotNull(response);
        assertEquals("Ошибка двойного ввода данных", response.getError());
        assertEquals("Двойной ввод данных", response.getDescription());
    }

    @Test
    void handleBookingIdException_ShouldReturnCorrectErrorResponse() {
        BookingIdException exception = new BookingIdException(1);
        ErrorHandler.ErrorResponse response = errorHandler.handleBookingIdException(exception);

        assertNotNull(response);
        assertEquals("Ошибка в id бронирования", response.getError());
        assertEquals("Бронирование с id 1 не найдено", response.getDescription());
    }

    @Test
    void handleValidationException_ShouldReturnCorrectErrorResponse() {
        ValidationException exception = new ValidationException("Ошибка валидации");
        ErrorHandler.ErrorResponse response = errorHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals("Ошибка валидации данных", response.getError());
        assertEquals("Ошибка валидации", response.getDescription());
    }

    @Test
    void handleRequestIdException_ShouldReturnCorrectErrorResponse() {
        RequestIdException exception = new RequestIdException(1);
        ErrorHandler.ErrorResponse response = errorHandler.handleRequestIdException(exception);

        assertNotNull(response);
        assertEquals("Ошибка в id запроса", response.getError());
        assertEquals("Запрос с id 1 не найден", response.getDescription());
    }


}
