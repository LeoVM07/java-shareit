package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                           @RequestBody @Valid ItemDto itemDto) {
        return new ResponseEntity<>(itemService.addItem(ownerId, itemDto), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> showItemById(@PathVariable("itemId") long itemId) {
        return new ResponseEntity<>(itemService.showItemById(itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> showAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return new ResponseEntity<>(itemService.showAllItemsByUserId(ownerId), HttpStatus.OK);
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
}

