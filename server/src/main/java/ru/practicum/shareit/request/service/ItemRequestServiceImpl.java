package ru.practicum.shareit.request.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestIdException;
import ru.practicum.shareit.exception.UserIdException;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.BlankItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;

import java.util.List;

@Data
@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(long userId, BlankItemRequestDto requestDto) {

        ItemRequest request = ItemRequestMapper.toItemRequestFromBlankDto(requestDto, userId);
        ItemRequest savedRequest = requestRepository.save(request);

        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }


    @Override
    public List<ItemRequestDto> getAllUserRequests(long requestorId) {

        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);

        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        List<ItemRequest> allRequests = requestRepository.findAll();
        return allRequests.stream().map(ItemRequestMapper::toItemRequestDto).toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long requestId) {

        userRepository.findById(userId).orElseThrow(() -> new UserIdException(userId));

        ItemRequest request = requestRepository.findById(requestId).
                orElseThrow(() -> new RequestIdException(requestId));

        return ItemRequestMapper.toItemRequestDto(request);

    }
}
