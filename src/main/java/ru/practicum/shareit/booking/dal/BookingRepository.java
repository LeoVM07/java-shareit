package ru.practicum.shareit.booking.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(
            long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker WHERE i.id IN :itemIds")
    List<Booking> findAllByItemIdIn(@Param("itemIds") List<Long> itemIds);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(
            Long userId, Long itemId, BookingStatus bookingStatus, LocalDateTime time);

    List<Booking> findByItemId(long itemId);
}
