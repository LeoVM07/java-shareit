package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItem_ShouldMapAllFieldsCorrectly() {

        User owner = new User(1L, "Owner Name", "owner@email.com");
        ItemRequest request = new ItemRequest(1L, "Need item", null, null, null);
        CommentDto commentDto = new CommentDto(1L, "Great item!", 1L, "Author Name", null);

        ItemDto itemDto = new ItemDto(
                1L,
                "Test Item",
                "Test Description",
                true,
                1L,
                null,
                null,
                List.of(commentDto),
                request.getId()
        );


        Item item = ItemMapper.toItemFromDto(itemDto, owner, request);

        assertThat(item.getId()).isEqualTo(itemDto.getId());
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(item.isAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(request);
        assertThat(item.getComments()).hasSize(1);
        assertThat(item.getComments().get(0).getId());
    }

}
