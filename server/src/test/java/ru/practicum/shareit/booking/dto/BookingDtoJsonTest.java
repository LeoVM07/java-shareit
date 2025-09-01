package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    private JacksonTester<BookingRequestDto> bookingRequestJson;
    @Autowired
    private JacksonTester<BookingDto> bookingJson;
    @Autowired
    private JacksonTester<BookingInfoDto> bookingInfoJson;

    @Test
    void testBookingRequestDto_Serialization() throws Exception {

        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 16, 10, 0, 0);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        bookingRequestDto.setItemId(1L);

        JsonContent<BookingRequestDto> result = bookingRequestJson.write(bookingRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-15T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-16T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
    }

    @Test
    void testBookingRequestDto_Deserialization() throws Exception {

        String jsonContent = "{\"start\": \"2024-01-15T10:00:00\", \"end\": \"2024-01-16T10:00:00\", \"itemId\": 1}";

        BookingRequestDto result = bookingRequestJson.parse(jsonContent).getObject();

        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 10, 0, 0));
        assertThat(result.getItemId()).isEqualTo(1L);
    }

    @Test
    void testBookingResponseDto_Serialization() throws Exception {

        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 16, 10, 0, 0);

        UserDto bookerDto = new UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Booker Name");
        bookerDto.setEmail("booker@test.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(bookerDto);
        bookingDto.setItem(itemDto);

        JsonContent<BookingDto> result = bookingJson.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-15T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-16T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo("Booker Name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("booker@test.com");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("Test Item");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("Test Description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isTrue();
    }

    @Test
    void testBookingResponseDto_Deserialization() throws Exception {

        String jsonContent = "{\"id\": 1, \"start\": \"2024-01-15T10:00:00\", \"end\": \"2024-01-16T10:00:00\", " +
                "\"status\": \"APPROVED\", \"booker\": {\"id\": 2, \"name\": \"Booker Name\", " +
                "\"email\": \"booker@test.com\"}, \"item\": {\"id\": 1, \"name\": \"Test Item\", " +
                "\"description\": \"Test Description\", \"available\": true}}";

        BookingDto result = bookingJson.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 10, 0, 0));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);

        assertThat(result.getBooker()).isNotNull();
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getBooker().getName()).isEqualTo("Booker Name");
        assertThat(result.getBooker().getEmail()).isEqualTo("booker@test.com");

        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(1L);
        assertThat(result.getItem().getName()).isEqualTo("Test Item");
        assertThat(result.getItem().getDescription()).isEqualTo("Test Description");
        assertThat(result.getItem().getAvailable()).isTrue();
    }

    @Test
    void testBookingShortDto_Serialization() throws Exception {

        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 16, 10, 0, 0);

        BookingInfoDto bookingShortDto = new BookingInfoDto();
        bookingShortDto.setId(1L);
        bookingShortDto.setStart(start);
        bookingShortDto.setEnd(end);

        JsonContent<BookingInfoDto> result = bookingInfoJson.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-15T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-16T10:00:00");
    }

    @Test
    void testBookingShortDto_Deserialization() throws Exception {

        String jsonContent = "{\"id\": 1, \"bookerId\": 2, \"start\": \"2024-01-15T10:00:00\", \"end\": \"2024-01-16T10:00:00\"}";

        BookingInfoDto result = bookingInfoJson.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 10, 0, 0));
    }

    @Test
    void testBookingRequestDto_WithNullValues() throws Exception {

        BookingRequestDto bookingRequestDto = new BookingRequestDto();

        JsonContent<BookingRequestDto> result = bookingRequestJson.write(bookingRequestDto);

        assertThat(result).extractingJsonPathValue("$.start").isNull();
        assertThat(result).extractingJsonPathValue("$.end").isNull();
    }

    @Test
    void testBookingResponseDto_WithDifferentStatuses() throws Exception {

        BookingDto rejectedBooking = new BookingDto();
        rejectedBooking.setId(1L);
        rejectedBooking.setStatus(BookingStatus.REJECTED);

        JsonContent<BookingDto> rejectedResult = bookingJson.write(rejectedBooking);
        assertThat(rejectedResult).extractingJsonPathStringValue("$.status")
                .isEqualTo("REJECTED");

        BookingDto canceledBooking = new BookingDto();
        canceledBooking.setId(2L);
        canceledBooking.setStatus(BookingStatus.CANCELED);

        JsonContent<BookingDto> canceledResult = bookingJson.write(canceledBooking);
        assertThat(canceledResult).extractingJsonPathStringValue("$.status")
                .isEqualTo("CANCELED");
    }

    @Test
    void testDateTimeFormat_EdgeCases() throws Exception {

        LocalDateTime complexDateTime = LocalDateTime.of(2024, 12, 31, 23, 59, 59, 999999999);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(complexDateTime);
        bookingRequestDto.setItemId(1L);

        JsonContent<BookingRequestDto> result = bookingRequestJson.write(bookingRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .startsWith("2024-12-31T23:59:59");
    }

    @Test
    void testInvalidDateTimeDeserialization() throws Exception {

        String invalidJsonContent = "{\"start\": \"invalid-date\", \"end\": \"2024-01-16T10:00:00\", \"itemId\": 1}";

        try {
            bookingRequestJson.parse(invalidJsonContent);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Cannot deserialize");
        }
    }
}
