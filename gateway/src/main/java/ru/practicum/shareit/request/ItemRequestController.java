package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.BlankItemRequestDto;

@Data
@RestController
@RequestMapping("/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId,
                                                    @RequestBody @Valid BlankItemRequestDto requestDto) {
        log.info("Отправлен запрос на вещь: {} от пользователя с id {}", requestDto, userId);
        return requestClient.createItemRequest(userId, requestDto);
    }


    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@RequestHeader("X-Sharer-User-Id")
                                                     @Positive @NotNull Long requestorId) {
        log.info("Выведен список запросов пользователя с id {}", requestorId);
        return requestClient.getAllUserRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        log.trace("Выведен список всех запросов на вещи");
        return requestClient.getAllItemRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId,
                                                     @PathVariable("requestId") @Positive @NotNull Long requestId) {
        log.info("Выведен запрос с id {} для пользователя с id {}", requestId, userId);
        return requestClient.getItemRequestById(userId, requestId);

    }
}
