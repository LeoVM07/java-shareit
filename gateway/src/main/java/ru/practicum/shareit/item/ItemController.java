package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Slf4j
@Data
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Добавление вещи {} от пользователя с id: {}", itemDto, ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @NotNull @PathVariable("itemId") Long itemId) {
        log.info("Вывод вещи с id: {}", itemId);
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(
            @Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Вывод всех вещей пользователя с id: {}", ownerId);
        return itemClient.getAllItemsByUserId(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @NotNull @PathVariable("itemId") Long itemId,
                                             @Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestBody @Valid ItemUpdateDto itemUpdate) {
        return itemClient.updateItem(itemId, ownerId, itemUpdate);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long authorId,
                                                @Positive @NotNull @PathVariable("itemId") long itemId,
                                                @RequestBody @Valid CommentCreateDto comment) {
        return itemClient.createComment(authorId, itemId, comment);
    }
}
