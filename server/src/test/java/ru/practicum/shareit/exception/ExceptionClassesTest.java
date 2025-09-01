package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ExceptionClassesTest {

    @Test
    void bookingException_WithMessage_CreateExceptionWithCorrectMessage() {
        String message = "Бронирование с id 1 не найдено";

        BookingIdException exception = new BookingIdException(1);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);

    }

    @Test
    void duplicateIdException_WithMessage_CreateExceptionWithCorrectMessage() {
        String message = "Попытка повторно внести данные";

        DuplicateIdException exception = new DuplicateIdException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void itemAvailabilityException_WithMessage_CreateExceptionWithCorrectMessage() {
        String message = "Вещь с id 1 не доступна для бронирования";

        ItemAvailabilityException exception = new ItemAvailabilityException(1);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void itemIdException_WithMessage_CreateExceptionWithCorrectMessage() {
        String message = "Вещь с id 1 не найдена";

        ItemIdException exception = new ItemIdException(1);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void requestIdException_WithMessage_CreateExceptionWithCorrectMessage() {
        String message = "Запрос с id 1 не найден";

        RequestIdException exception = new RequestIdException(1);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void userEmailException_WithMessage_CreateExceptionWithCorrectMessage() {
        String message = "Email kekozavr@sobaka.com уже используется другим пользователем!";

        UserEmailException exception = new UserEmailException("kekozavr@sobaka.com");

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void userIdException_WithMessage_CreateExceptionWithCorrectMessage() {
        String message = "Пользователь с id 1 не найден";

        UserIdException exception = new UserIdException(1);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void validationException_WithMessage_CreateExceptionWithCorrectMessage() {
        String message = "Если вы думаете, что на что-то способны, вы правы; " +
                "если думаете, что у вас ничего не получится - вы тоже правы";

        ValidationException exception = new ValidationException("Если вы думаете, что на что-то способны, вы правы; " +
                "если думаете, что у вас ничего не получится - вы тоже правы");

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

}