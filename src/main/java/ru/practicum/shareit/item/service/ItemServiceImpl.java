package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemIdException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Data
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        User owner = checkUser(ownerId);
        Item item = ItemMapper.toItemFromDto(itemDto);
        item.setOwner(owner);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemBookedDto getItemById(long itemId) {
        Item item = checkItem(itemId);

        ItemBookedDto itemDto = ItemMapper.toItemBookedDto(item);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments
                .stream()
                .map(CommentMapper::toDtoFromComment)
                .toList();
        itemDto.setComments(commentDtos);

        return itemDto;
    }

    public List<ItemBookedDto> getAllItemsByUserId(long ownerId) {

        LocalDateTime current = LocalDateTime.now();
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, ItemBookedDto> itemDtos = items
                .stream()
                .map(ItemMapper::toItemBookedDto)
                .collect(Collectors.toMap(ItemBookedDto::getId, item -> item));

        List<Booking> itemsBooked = bookingRepository.findAllByItemIdIn(itemIds);

        for (Booking booking : itemsBooked) {
            long itemId = booking.getItem().getId();


            if (booking.getStart().isAfter(current)) {
                itemDtos.get(itemId).setNextBooking(
                        new BookingInfoDto(
                                booking.getId(),
                                booking.getStart(),
                                booking.getEnd())
                );
            }

            if (booking.getEnd().isBefore(current)) {
                itemDtos.get(itemId).setLastBooking(
                        new BookingInfoDto(
                                booking.getId(),
                                booking.getStart(),
                                booking.getEnd()
                        )
                );
            }

        }

        return itemDtos.values().stream().toList();
    }

    public ItemDto updateItem(long itemId, long ownerId, ItemUpdateDto itemUpdate) {
        Item itemToUpdate = checkItem(itemId);

        if (itemToUpdate.getOwner().getId() != ownerId) {
            log.error("У пользователя с id {} не найдено вещи с id {}", ownerId, itemId);
            throw new ItemIdException(itemId);
        }

        String newName = itemUpdate.getName();
        String newDescription = itemUpdate.getDescription();
        Boolean newAvailable = itemUpdate.getAvailable();

        if (newName != null && !newName.isBlank()) {
            itemToUpdate.setName(newName);
        }

        if (newDescription != null && !newDescription.isBlank()) {
            itemToUpdate.setDescription(newDescription);
        }
        if (newAvailable != null) {
            itemToUpdate.setAvailable(newAvailable);
        }

        log.info("Вещь с id {} была обновлена", itemId);
        itemRepository.save(itemToUpdate);
        return ItemMapper.toItemDto(itemToUpdate);
    }

    public List<ItemDto> searchItems(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Выведена информация по поиску. Текст поиска: {}", searchText);
        return itemRepository.findAvailableItemsWithText(searchText).stream().map(ItemMapper::toItemDto).toList();
    }

    public CommentDto createComment(long authorId, long itemId, CommentDto commentDto) {
        User author = checkUser(authorId);
        Item item = checkItem(itemId);

        List<Booking> userBookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                authorId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("Отзывы можно оставить только к вещам, которые были взяты в аренду");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated();

        commentRepository.save(comment);
        return CommentMapper.toDtoFromComment(comment);
    }

    private User checkUser(long userId) {
        return UserMapper.toUserFromDto(userService.getUserById(userId));
    }

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemIdException(itemId));
    }
}
