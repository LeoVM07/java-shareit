package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemBookedDto getItemById(long itemId);

    List<ItemBookedDto> getAllItemsByUserId(long ownerId);

    ItemDto updateItem(long itemId, long ownerId, ItemUpdateDto itemUpdate);

    List<ItemDto> searchItems(String searchText);

    CommentDto createComment(long authorId, long itemId, CommentDto commentDto);
}
