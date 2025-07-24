package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Data
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @RequestBody @Valid ItemDto itemDto) {
        return new ResponseEntity<>(itemService.createItem(ownerId, itemDto), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemBookedDto> getItemById(@PathVariable("itemId") long itemId) {
        return new ResponseEntity<>(itemService.getItemById(itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemBookedDto>> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return new ResponseEntity<>(itemService.getAllItemsByUserId(ownerId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable("itemId") long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @RequestBody ItemUpdateDto itemUpdate) {
        return new ResponseEntity<>(itemService.updateItem(itemId, ownerId, itemUpdate), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        return new ResponseEntity<>(itemService.searchItems(text), HttpStatus.OK);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                                    @PathVariable("itemId") long itemId,
                                                    @RequestBody CommentDto comment) {
        return new ResponseEntity<>(itemService.createComment(authorId, itemId, comment), HttpStatus.OK);
    }
}

