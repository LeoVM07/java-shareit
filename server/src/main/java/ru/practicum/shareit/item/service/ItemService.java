package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto getItemById(long itemId);

    List<ItemDto> getAllItemsByUserId(long ownerId);

    ItemDto updateItem(long itemId, long ownerId, ItemUpdateDto itemUpdate);

    List<ItemDto> searchItems(String searchText);

    CommentDto createComment(long authorId, long itemId, CommentCreateDto commentDto);
}
