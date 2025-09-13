package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingIdException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @AfterEach
    void tearDown() {

        entityManager.createQuery("DELETE FROM bookings").executeUpdate();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createBooking_IntegrationTest_ShouldCreateAndSaveBooking() {

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.createBooking(bookingRequestDto, booker.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());

        TypedQuery<Booking> query = entityManager.createQuery(
                "SELECT b FROM bookings b WHERE b.id = :id", Booking.class);
        query.setParameter("id", result.getId());
        Booking savedBooking = query.getSingleResult();

        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void updateBookingStatus_IntegrationTest_ShouldUpdateStatusInDatabase() {

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.createBooking(bookingRequestDto, booker.getId());

        BookingDto result = bookingService.updateBooking(
                createdBooking.getId(), owner.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);

        TypedQuery<Booking> query = entityManager.createQuery(
                "SELECT b FROM bookings b WHERE b.id = :id", Booking.class);
        query.setParameter("id", result.getId());
        Booking updatedBooking = query.getSingleResult();

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getUserBookings_IntegrationTest_ShouldReturnUserBookingsFromDatabase() {

        BookingRequestDto bookingRequestDto1 = new BookingRequestDto();
        bookingRequestDto1.setItemId(item.getId());
        bookingRequestDto1.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto1.setEnd(LocalDateTime.now().plusDays(2));

        BookingRequestDto bookingRequestDto2 = new BookingRequestDto();
        bookingRequestDto2.setItemId(item.getId());
        bookingRequestDto2.setStart(LocalDateTime.now().plusDays(3));
        bookingRequestDto2.setEnd(LocalDateTime.now().plusDays(4));

        bookingService.createBooking(bookingRequestDto1, booker.getId());
        bookingService.createBooking(bookingRequestDto2, booker.getId());

        List<BookingDto> result = bookingService.getAllBookingsByUserId(booker.getId(), "ALL");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(booking -> booking.getBooker().getId().equals(booker.getId()));

        assertThat(result.get(0).getStart()).isAfter(result.get(1).getStart());
    }

    @Test
    void getUserBookings_WithStateFuture_IntegrationTest_ShouldReturnOnlyFutureBookings() {

        BookingRequestDto pastBooking = new BookingRequestDto();
        pastBooking.setItemId(item.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));

        BookingRequestDto futureBooking = new BookingRequestDto();
        futureBooking.setItemId(item.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        Booking pastBookingEntity = new Booking();
        pastBookingEntity.setStart(pastBooking.getStart());
        pastBookingEntity.setEnd(pastBooking.getEnd());
        pastBookingEntity.setItem(item);
        pastBookingEntity.setBooker(booker);
        pastBookingEntity.setStatus(BookingStatus.APPROVED);
        entityManager.persist(pastBookingEntity);

        bookingService.createBooking(futureBooking, booker.getId());

        List<BookingDto> result = bookingService.getAllBookingsByUserId(booker.getId(), "FUTURE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStart()).isAfter(LocalDateTime.now());
    }

    @Test
    void getOwnerBookings_IntegrationTest_ShouldReturnOwnerBookingsFromDatabase() {

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.createBooking(bookingRequestDto, booker.getId());

        List<BookingDto> result = bookingService.getAllBookingsByOwner(owner.getId(), "ALL");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getId()).isEqualTo(item.getId());
    }


    @Test
    void updateBookingStatus_WhenBookingNotExists_IntegrationTest_ShouldThrowNotFoundException() {

        assertThatThrownBy(() -> bookingService.updateBooking(999L, owner.getId(), true))
                .isInstanceOf(BookingIdException.class)
                .hasMessage("Бронирование с id 999 не найдено");
    }

    @Test
    void getBookingById_WhenBookingExists_IntegrationTest_ShouldReturnBookingFromDatabase() {

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.createBooking(bookingRequestDto, booker.getId());

        BookingDto result = bookingService.getBookingById(createdBooking.getId(), booker.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(createdBooking.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }
}
