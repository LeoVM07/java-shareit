package ru.practicum.shareit.item.dal.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface InMemoryItemStorage {

    Item addItem(ItemDto itemDto);

    ItemDto showItemById(long itemId);

    List<ItemDto> showAllItemsByUserId(long ownerId);

    ItemDto updateItem(long itemId, long ownerId, ItemUpdateDto itemUpdate);

    List<ItemDto> searchItems(String searchText);
}
