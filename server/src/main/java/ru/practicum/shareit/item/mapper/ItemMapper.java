package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        List<CommentDto> commentDtos = item.getComments() != null ?
                item.getComments().stream()
                        .map(CommentMapper::toDtoFromComment)
                        .toList() :
                Collections.emptyList();

        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner().getId(),
                null,
                null,
                commentDtos,
                item.getRequest() != null ? item.getRequest().getId() : null
        );

        itemDto.setId(item.getId());
        itemDto.setOwnerId(item.getOwner().getId());
        return itemDto;
    }

    public static Item toItemFromDto(ItemDto itemDto, User owner, ItemRequest request) {
        List<Comment> comments = itemDto.getComments() != null ?
                itemDto.getComments().stream()
                        .map(CommentMapper::toCommentFromDto)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                request,
                comments
        );
    }
}
