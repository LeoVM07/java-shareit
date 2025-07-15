package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dal.InMemoryItemStorageImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Data
@Service
public class ItemService {

    private final InMemoryItemStorageImpl itemStorage; //поменять на интерфейс!
    private final InMemoryUserStorage userStorage;


    public ItemDto addItem(long ownerId, ItemDto itemDto) {
        User owner = UserMapper.toUserFromDto(userStorage.showUser(ownerId));
        Item item = itemStorage.addItem(itemDto);

        item.setOwner(owner);
        itemDto.setOwnerId(ownerId);
        log.info("Вещь с id {} была добавлена", item.getId());
        return itemDto;
    }

    public ItemDto showItemById(long itemId) {
        return itemStorage.showItemById(itemId);
    }

    public List<ItemDto> showAllItemsByUserId(long ownerId) {
        return itemStorage.showAllItemsByUserId(ownerId);
    }

    public ItemDto updateItem(long itemId, long ownerId, ItemUpdateDto itemUpdate) {
        return itemStorage.updateItem(itemId, ownerId, itemUpdate);
    }

    public List<ItemDto> searchItems(String searchText) {
        return itemStorage.searchItems(searchText);
    }
}
