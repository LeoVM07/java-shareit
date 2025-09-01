package ru.practicum.shareit.booking.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Data
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public BookingDto createBooking(BookingRequestDto requestDto, long userId) {

        User booker = checkUser(userId);
        Item item = checkItem(requestDto.getItemId());

        if (!item.isAvailable()) {
            log.error("Вещь с id {} не доступна для бронирования", item.getId());
            throw new ItemAvailabilityException(item.getId());
        }

        if (item.getOwner().getId() == userId) {
            throw new DuplicateIdException("Пользователь не может забронировать свою же вещь");
        }

        Booking booking = new Booking();
        booking.setStart(requestDto.getStart());
        booking.setEnd(requestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Бронирование с id {} было успешно создано", booking.getId());
        return BookingMapper.toDtoFromBooking(savedBooking);
    }

    @Override
    public BookingDto updateBooking(long bookingId, long ownerId, boolean approved) {
        Booking booking = checkBooking(bookingId);

        if (booking.getItem().getOwner().getId() != ownerId) {
            log.error("Попытка изменить статус бронирования чужой вещи");
            throw new ValidationException("Только владелец может изменить статус бронирования вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            log.error("Попытка изменения статуса уже обработанного бронирования");
            throw new ValidationException(String.format("Бронирование с id %d уже было обработано", bookingId));
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toDtoFromBooking(booking);
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = checkBooking(bookingId);

        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            log.error("Попытка получить информацию о бронировании. Id пользователя: {}, id владельца: {}, " +
                    "id забронировавшего: {}", userId, booking.getItem().getOwner().getId(), booking.getBooker().getId());
            throw new ValidationException("Информацию о бронировании может получить только владелец вещи или тот," +
                    " кто её забронировал");
        }
        return BookingMapper.toDtoFromBooking(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(long userId, String state) {
        checkUser(userId);
        List<Booking> bookingList;
        LocalDateTime current = LocalDateTime.now();
        bookingList = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, current, current);
            case "PAST" -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, current);
            case "FUTURE" -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, current);
            case "WAITING" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> {
                log.error("Некорректный параметр state: {}", state);
                throw new ValidationException(String.format("Введён некорректный параметр запроса: %s", state));
            }
        };
        log.info("Выведен список бронирований для пользователя с id {} в соответствии с параметром поиска: {}",
                userId, state);
        return bookingList.stream().map(BookingMapper::toDtoFromBooking).toList();
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(long ownerId, String state) {
        checkUser(ownerId);
        List<Booking> bookingList;
        LocalDateTime current = LocalDateTime.now();
        bookingList = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case "CURRENT" -> bookingRepository
                    .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, current, current);
            case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, current);
            case "FUTURE" -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, current);
            case "WAITING" ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case "REJECTED" ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            default -> {
                log.error("Некорректный параметр state: {}", state);
                throw new ValidationException(String.format("Введён некорректный параметр запроса: %s", state));
            }
        };
        log.info("Выведен список бронирований для пользователя с id {} в соответствии с параметром поиска: {}",
                ownerId, state);
        return bookingList.stream().map(BookingMapper::toDtoFromBooking).toList();
    }


    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserIdException(userId));
    }

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemIdException(itemId));
    }

    private Booking checkBooking(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingIdException(bookingId));
    }

}
