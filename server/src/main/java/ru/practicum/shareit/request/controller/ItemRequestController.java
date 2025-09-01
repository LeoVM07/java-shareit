package ru.practicum.shareit.request.controller;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.BlankItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping("/requests")
@Data
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestHeader("X-Sharer-User-Id")
                                                            Long requestorId,
                                                            @RequestBody BlankItemRequestDto requestDto) {
        return new ResponseEntity<>(requestService.createItemRequest(requestorId, requestDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllUserRequests(@RequestHeader("X-Sharer-User-Id")
                                                                   Long requestorId) {
        return new ResponseEntity<>(requestService.getAllUserRequests(requestorId), HttpStatus.OK);

    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequests() {
        return new ResponseEntity<>(requestService.getAllItemRequests(), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@RequestHeader("X-Sharer-User-Id")
                                                             Long userId,
                                                             @PathVariable("requestId") long requestId) {
        return new ResponseEntity<>(requestService.getItemRequestById(userId, requestId), HttpStatus.OK);
    }

}
