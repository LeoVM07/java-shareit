package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.exception.ItemIdException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private UserDto ownerDto;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("John Doe");
        owner.setEmail("john@email.com");

        ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("John Doe");
        ownerDto.setEmail("john@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setComments(Collections.emptyList());

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setComments(Collections.emptyList());

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("John Doe");
        commentDto.setCreated(LocalDateTime.now());

        commentCreateDto = new CommentCreateDto(commentDto.getText());
    }


    @Test
    void addItem_ShouldReturnItemDto_WhenValidInput() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(1L, itemDto);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.isAvailable(), result.getAvailable());

        verify(userRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void addItem_ShouldSetRequest_WhenRequestIdProvided() {

        itemDto.setRequestId(2L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);


        ItemDto result = itemService.createItem(1L, itemDto);

        assertNotNull(result);
        verify(itemRepository).save(argThat(savedItem ->
                savedItem.getRequest() != null && savedItem.getRequest().getId().equals(2L)));
    }

    @Test
    void updateItem_ShouldUpdateItem_WhenOwnerIsCorrect() {

        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        verify(itemRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenItemNotFound() {

        ItemUpdateDto updateDto = new ItemUpdateDto();
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemIdException.class,
                () -> itemService.updateItem(1L, 1L, updateDto));

        verify(itemRepository).findById(1L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenUserIsNotOwner() {

        ItemUpdateDto updateDto = new ItemUpdateDto();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ItemIdException.class,
                () -> itemService.updateItem(1L, 999L, updateDto));

        verify(itemRepository).findById(1L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldUpdateOnlyProvidedFields() {

        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Name");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemService.updateItem(1L, 1L, updateDto);

        verify(itemRepository).save(argThat(savedItem ->
                savedItem.getName().equals("Updated Name") &&
                        savedItem.getDescription().equals("Test Description") &&
                        savedItem.isAvailable()
        ));
    }

    @Test
    void getItemById_ShouldThrowNotFoundException_WhenItemNotFound() {

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemIdException.class,
                () -> itemService.getItemById(1L));

        verify(itemRepository).findById(1L);
        verify(commentRepository, never()).findByItemIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getAllItemsByOwner_ShouldReturnItemsList() {

        List<Item> items = List.of(item);
        when(itemRepository.findByOwnerId(1L)).thenReturn(items);

        List<ItemDto> result = itemService.getAllItemsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getName(), result.get(0).getName());

        verify(itemRepository).findByOwnerId(1L);
    }

    @Test
    void getAllItemsByOwner_ShouldReturnEmptyList_WhenNoItems() {

        when(itemRepository.findByOwnerId(1L)).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getAllItemsByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository).findByOwnerId(1L);
    }

    @Test
    void searchItems_ShouldReturnItemsList_WhenTextProvided() {

        String searchText = "test";
        List<Item> items = List.of(item);
        when(itemRepository.findAvailableItemsWithText(searchText.toLowerCase())).thenReturn(items);

        List<ItemDto> result = itemService.searchItems(searchText);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getName(), result.get(0).getName());

        verify(itemRepository).findAvailableItemsWithText(searchText.toLowerCase());
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsNull() {

        List<ItemDto> result = itemService.searchItems(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, never()).findAvailableItemsWithText(anyString());
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsBlank() {

        List<ItemDto> result = itemService.searchItems("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, never()).findAvailableItemsWithText(anyString());
    }
}
