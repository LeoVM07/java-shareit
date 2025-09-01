package ru.practicum.shareit.request.mapper;


import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.BlankItemRequestDto;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequestFromBlankDto(BlankItemRequestDto requestDto, long userId) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequestorId(userId);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {

        List<ItemForRequestDto> items = request.getItems() != null
                ? request.getItems().stream()
                .map(ItemRequestMapper::toItemForRequest)
                .toList()
                : List.of();

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestorId(),
                request.getCreated(),
                items);
    }

    private static ItemForRequestDto toItemForRequest(Item item) {
        return new ItemForRequestDto(item.getId(), item.getName(), item.getOwner().getId());
    }
}
