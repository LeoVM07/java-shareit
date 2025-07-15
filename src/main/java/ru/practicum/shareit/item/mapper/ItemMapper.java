package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );

        itemDto.setId(item.getId());
        itemDto.setOwnerId(item.getOwner().getId());
        return itemDto;
    }

    public static Item toItemFromDto(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }
}
