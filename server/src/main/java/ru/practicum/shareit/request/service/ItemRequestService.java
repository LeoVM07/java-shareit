package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.BlankItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(long userId, BlankItemRequestDto requestDto);

    List<ItemRequestDto> getAllUserRequests(long requestorId);

    List<ItemRequestDto> getAllItemRequests();

    ItemRequestDto getItemRequestById(long userId, long requestId);
}
