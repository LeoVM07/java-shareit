package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Item unavailableItem;
    private BookingRequestDto bookingRequestDto;
    private Booking booking;
    private Booking approvedBooking;
    private Booking rejectedBooking;

    @BeforeEach
    void setUp() {

        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@test.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@test.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        unavailableItem = new Item();
        unavailableItem.setId(2L);
        unavailableItem.setName("Unavailable Item");
        unavailableItem.setDescription("Unavailable Description");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(owner);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        approvedBooking = new Booking();
        approvedBooking.setId(2L);
        approvedBooking.setStart(LocalDateTime.now().minusDays(2));
        approvedBooking.setEnd(LocalDateTime.now().minusDays(1));
        approvedBooking.setItem(item);
        approvedBooking.setBooker(booker);
        approvedBooking.setStatus(BookingStatus.APPROVED);

        rejectedBooking = new Booking();
        rejectedBooking.setId(3L);
        rejectedBooking.setStart(LocalDateTime.now().plusDays(3));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(4));
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
    }

    @Test
    void createBooking_WhenValidData_ShouldReturnBookingResponseDto() {

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(bookingRequestDto, 2L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getItem().getId()).isEqualTo(1L);

        verify(bookingRepository).save(any(Booking.class));
        verify(userRepository).findById(2L);
        verify(itemRepository).findById(1L);
    }

    @Test
    void createBooking_WhenUserNotFound_ShouldThrowNotFoundException() {

        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequestDto, 10L))
                .isInstanceOf(UserIdException.class)
                .hasMessage("Пользователь с id 10 не найден");

        verify(userRepository).findById(10L);
        verifyNoInteractions(itemRepository, bookingRepository);
    }

    @Test
    void createBooking_WhenItemNotFound_ShouldThrowNotFoundException() {

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequestDto, 2L))
                .isInstanceOf(ItemIdException.class)
                .hasMessage("Вещь с id 1 не найдена");

        verify(userRepository).findById(2L);
        verify(itemRepository).findById(1L);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_WhenItemNotAvailable_ShouldThrowBadRequestException() {

        bookingRequestDto.setItemId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(unavailableItem));

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequestDto, 2L))
                .isInstanceOf(ItemAvailabilityException.class)
                .hasMessage("Вещь с id 2 не доступна для бронирования");

        verify(userRepository).findById(2L);
        verify(itemRepository).findById(2L);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_WhenOwnerTriesToBook_ShouldThrowBadRequestException() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequestDto, 1L))
                .isInstanceOf(DuplicateIdException.class)
                .hasMessage("Пользователь не может забронировать свою же вещь");

        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void updateBookingStatus_WhenValidApproval_ShouldReturnApprovedBooking() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.updateBooking(1L, 1L, true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBookingStatus_WhenValidRejection_ShouldReturnRejectedBooking() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.updateBooking(1L, 1L, false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBookingStatus_WhenBookingNotFound_ShouldThrowNotFoundException() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBooking(1L, 1L, true))
                .isInstanceOf(BookingIdException.class)
                .hasMessage("Бронирование с id 1 не найдено");

        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_WhenNotOwner_ShouldThrowBadRequestException() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBooking(1L, 999L, true))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Только владелец может изменить статус бронирования вещи");

        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_WhenBookingAlreadyApproved_ShouldThrowBadRequestException() {

        approvedBooking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(approvedBooking));

        assertThatThrownBy(() -> bookingService.updateBooking(2L, 1L, true))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Бронирование с id 2 уже было обработано");

        verify(bookingRepository).findById(2L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingById_WhenBookerRequests_ShouldReturnBooking() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBookingById_WhenOwnerRequests_ShouldReturnBooking() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBookingById_WhenBookingNotFound_ShouldThrowNotFoundException() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(1L, 2L))
                .isInstanceOf(BookingIdException.class)
                .hasMessage("Бронирование с id 1 не найдено");

        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBookingById_WhenUnauthorizedUser_ShouldThrowConflictException() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(1L, 999L))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Информацию о бронировании может получить только владелец вещи или тот," +
                        " кто её забронировал");

        verify(bookingRepository).findById(1L);
    }

    @Test
    void getUserBookings_WithStateAll_ShouldReturnAllBookings() {

        List<Booking> bookings = Arrays.asList(booking, approvedBooking);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(2L, "ALL");

        assertThat(result).hasSize(2);
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L);
    }

    @Test
    void getUserBookings_WithStateCurrent_ShouldReturnCurrentBookings() {

        List<Booking> bookings = Arrays.asList(booking);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(2L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(2L, "CURRENT");

        assertThat(result).hasSize(1);
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(2L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getUserBookings_WithStatePast_ShouldReturnPastBookings() {

        List<Booking> bookings = Arrays.asList(approvedBooking);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(2L, "PAST");

        assertThat(result).hasSize(1);
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdAndEndBeforeOrderByStartDesc(eq(2L), any(LocalDateTime.class));
    }

    @Test
    void getUserBookings_WithStateFuture_ShouldReturnFutureBookings() {

        List<Booking> bookings = Arrays.asList(booking);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(2L, "FUTURE");

        assertThat(result).hasSize(1);
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdAndStartAfterOrderByStartDesc(eq(2L), any(LocalDateTime.class));
    }

    @Test
    void getUserBookings_WithStateWaiting_ShouldReturnWaitingBookings() {

        List<Booking> bookings = Arrays.asList(booking);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(2L, "WAITING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING);
    }

    @Test
    void getUserBookings_WithInvalidState_ShouldThrowConflictException() {

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));

        assertThatThrownBy(() -> bookingService.getAllBookingsByUserId(2L, "INVALID"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Введён некорректный параметр запроса: INVALID");

        verify(userRepository).findById(2L);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getUserBookings_WhenUserNotFound_ShouldThrowNotFoundException() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getAllBookingsByUserId(999L, "ALL"))
                .isInstanceOf(UserIdException.class)
                .hasMessage("Пользователь с id 999 не найден");

        verify(userRepository).findById(999L);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getUserBookings_WithEmptyResult_ShouldReturnEmptyList() {

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L)).thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingService.getAllBookingsByUserId(2L, "ALL");

        assertThat(result).isEmpty();
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L);
    }

    @Test
    void getOwnerBookings_WithStateAll_ShouldReturnAllOwnerBookings() {

        List<Booking> bookings = Arrays.asList(booking, approvedBooking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(1L, "ALL");

        assertThat(result).hasSize(2);
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L);
    }

    @Test
    void getOwnerBookings_WithStateCurrent_ShouldReturnCurrentOwnerBookings() {

        List<Booking> bookings = Arrays.asList(booking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(1L, "CURRENT");

        assertThat(result).hasSize(1);
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getOwnerBookings_WithStatePast_ShouldReturnPastOwnerBookings() {

        List<Booking> bookings = Arrays.asList(approvedBooking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(1L, "PAST");

        assertThat(result).hasSize(1);
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getOwnerBookings_WithStateFuture_ShouldReturnFutureOwnerBookings() {

        List<Booking> bookings = Arrays.asList(booking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(1L, "FUTURE");

        assertThat(result).hasSize(1);
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getOwnerBookings_WithStateWaiting_ShouldReturnWaitingOwnerBookings() {

        List<Booking> bookings = Arrays.asList(booking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(1L, "WAITING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING);
    }

    @Test
    void getOwnerBookings_WithInvalidState_ShouldThrowConflictException() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> bookingService.getAllBookingsByOwner(1L, "INVALID"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Введён некорректный параметр запроса: INVALID");

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getOwnerBookings_WhenUserNotFound_ShouldThrowNotFoundException() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getAllBookingsByOwner(999L, "ALL"))
                .isInstanceOf(UserIdException.class)
                .hasMessage("Пользователь с id 999 не найден");

        verify(userRepository).findById(999L);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getOwnerBookings_WithEmptyResult_ShouldReturnEmptyList() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L)).thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingService.getAllBookingsByOwner(1L, "ALL");

        assertThat(result).isEmpty();
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L);
    }

    @Test
    void updateBookingStatus_WhenApprovalIsNull_ShouldHandleCorrectly() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.updateBooking(1L, 1L, false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(booking);
    }

    @Test
    void createBooking_ShouldSetCorrectBookingFields() {

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setId(1L);
            return savedBooking;
        });

        BookingDto result = bookingService.createBooking(bookingRequestDto, 2L);

        verify(bookingRepository).save(argThat(savedBooking ->
                savedBooking.getStart().equals(bookingRequestDto.getStart()) &&
                        savedBooking.getEnd().equals(bookingRequestDto.getEnd()) &&
                        savedBooking.getItem().equals(item) &&
                        savedBooking.getBooker().equals(booker) &&
                        savedBooking.getStatus().equals(BookingStatus.WAITING)
        ));
    }

}
