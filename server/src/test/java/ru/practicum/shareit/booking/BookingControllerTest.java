package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingIdException;
import ru.practicum.shareit.exception.UserIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.UserHeaderConstant.X_SHARER_USER_ID;


@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking_WhenValidRequest_ShouldReturnBookingDto() throws Exception {

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        UserDto bookerDto = new UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Booker");
        bookerDto.setEmail("booker@test.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStart(requestDto.getStart());
        responseDto.setEnd(requestDto.getEnd());
        responseDto.setStatus(BookingStatus.WAITING);
        responseDto.setBooker(bookerDto);
        responseDto.setItem(itemDto);

        when(bookingService.createBooking(any(BookingRequestDto.class), eq(2L)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.item.id", is(1)));
    }

    @Test
    void createBooking_WhenUserNotFound_ShouldReturnNotFound() throws Exception {

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingService.createBooking(any(BookingRequestDto.class), eq(999L)))
                .thenThrow(new UserIdException(999L));

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookingStatus_WhenValidApproval_ShouldReturnUpdatedBooking() throws Exception {

        UserDto bookerDto = new UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Booker");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStatus(BookingStatus.APPROVED);
        responseDto.setBooker(bookerDto);
        responseDto.setItem(itemDto);

        when(bookingService.updateBooking(1L, 1L, true))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void updateBookingStatus_WhenValidRejection_ShouldReturnUpdatedBooking() throws Exception {

        UserDto bookerDto = new UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Booker");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStatus(BookingStatus.REJECTED);
        responseDto.setBooker(bookerDto);
        responseDto.setItem(itemDto);

        when(bookingService.updateBooking(1L, 1L, false))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }

    @Test
    void updateBookingStatus_WhenBookingNotFound_ShouldReturnNotFound() throws Exception {

        when(bookingService.updateBooking(999L, 1L, true))
                .thenThrow(new BookingIdException(999L));

        mockMvc.perform(patch("/bookings/999")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingById_WhenValidRequest_ShouldReturnBooking() throws Exception {
        // Given
        UserDto bookerDto = new UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Booker");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStart(LocalDateTime.now().plusDays(1));
        responseDto.setEnd(LocalDateTime.now().plusDays(2));
        responseDto.setStatus(BookingStatus.WAITING);
        responseDto.setBooker(bookerDto);
        responseDto.setItem(itemDto);

        when(bookingService.getBookingById(1L, 2L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1")
                        .header(X_SHARER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.item.id", is(1)));
    }

    @Test
    void getUserBookings_WhenValidRequest_ShouldReturnBookingsList() throws Exception {

        UserDto bookerDto = new UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Booker");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");

        BookingDto booking1 = new BookingDto();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setBooker(bookerDto);
        booking1.setItem(itemDto);

        BookingDto booking2 = new BookingDto();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(bookerDto);
        booking2.setItem(itemDto);

        List<BookingDto> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getAllBookingsByUserId(2L, "ALL"))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, 2L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void getUserBookings_WhenDefaultState_ShouldReturnBookingsList() throws Exception {

        List<BookingDto> bookings = Arrays.asList(new BookingDto());

        when(bookingService.getAllBookingsByUserId(2L, "ALL"))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getOwnerBookings_WhenValidRequest_ShouldReturnOwnerBookingsList() throws Exception {

        UserDto bookerDto = new UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Booker");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");

        BookingDto booking = new BookingDto();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(bookerDto);
        booking.setItem(itemDto);

        List<BookingDto> bookings = Arrays.asList(booking);

        when(bookingService.getAllBookingsByOwner(1L, "ALL"))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getOwnerBookings_WhenDefaultState_ShouldReturnOwnerBookingsList() throws Exception {

        List<BookingDto> bookings = Arrays.asList(new BookingDto());

        when(bookingService.getAllBookingsByOwner(1L, "ALL"))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getOwnerBookings_WhenUserNotFound_ShouldReturnNotFound() throws Exception {

        when(bookingService.getAllBookingsByOwner(999L, "ALL"))
                .thenThrow(new UserIdException(999L));

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, 999L)
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_WhenMissingUserHeader_ShouldReturnBadRequest() throws Exception {

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_WhenInvalidJson_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
}
