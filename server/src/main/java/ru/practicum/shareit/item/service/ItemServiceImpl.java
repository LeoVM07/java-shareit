package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemIdException;
import ru.practicum.shareit.exception.UserIdException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        User owner = checkUser(ownerId);

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = new ItemRequest();
            request.setId(itemDto.getRequestId());
            item.setRequest(request);
        }

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = checkItem(itemId);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments
                .stream()
                .map(CommentMapper::toDtoFromComment)
                .toList();
        itemDto.setComments(commentDtos);

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
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

    @Override
    public List<ItemDto> searchItems(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Выведена информация по поиску. Текст поиска: {}", searchText);
        return itemRepository.findAvailableItemsWithText(searchText).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentCreateDto commentDto) {
        User author = checkUser(userId);
        Item item = checkItem(itemId);

        List<Booking> userBookings = bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("Отзывы можно оставить только к вещам, которые были взяты в аренду");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toDtoFromComment(savedComment);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserIdException(userId));
    }

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemIdException(itemId));
    }
}
