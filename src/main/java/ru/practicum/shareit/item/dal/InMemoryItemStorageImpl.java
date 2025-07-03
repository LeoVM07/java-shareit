package ru.practicum.shareit.item.dal;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemIdException;
import ru.practicum.shareit.item.dal.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class InMemoryItemStorageImpl implements InMemoryItemStorage {

    private long itemId = 1;
    private final Map<Long, Item> allItems = new HashMap<>();

    @Override
    public Item addItem(ItemDto itemDto) {
        Item item = ItemMapper.toItemFromDto(itemDto);

        item.setId(generateId());
        itemDto.setId(item.getId());

        allItems.put(item.getId(), item);

        log.info("Вещь с id {} была сформирована в объект 'Item'", item.getId());
        return item;
    }

    @Override
    public ItemDto showItemById(long itemId) {
        checkItemId(itemId);
        log.info("Вещь с id {} была выведена на экран", itemId);
        return ItemMapper.toItemDto(allItems.get(itemId));
    }

    @Override
    public List<ItemDto> showAllItemsByUserId(long ownerId) {
        log.trace("Список вещей пользователя с id {} был выведен на экран", ownerId);
        return allItems.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto updateItem(long itemId, long ownerId, ItemUpdateDto itemUpdate) {
        checkItemId(itemId);
        Item itemToUpdate = allItems.get(itemId);

        if (itemToUpdate.getOwner().getId() != ownerId) {
            log.error("У пользователя с id {} нет вещи с id {}", ownerId, itemId);
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
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            log.info("Пустой текст запроса");
            return Collections.emptyList();
        }

        String lowerCaseText = searchText.toLowerCase();
        log.info("Результат поиска по слову: '{}'", searchText);
        return allItems.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText)
                        || item.getDescription().toLowerCase().contains(lowerCaseText))
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private long generateId() {
        return itemId++;
    }

    private void checkItemId(long itemId) {
        if (!allItems.containsKey(itemId)) {
            throw new ItemIdException(itemId);
        }
    }

}
