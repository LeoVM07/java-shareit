package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemIdException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.UserHeaderConstant.X_SHARER_USER_ID;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private ItemDto itemDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentDto commentDto;
    private CommentCreateDto commentCreateDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@email.com");

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setComments(Collections.emptyList());

        itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Item");
        itemUpdateDto.setDescription("Updated Description");
        itemUpdateDto.setAvailable(false);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("Test User");
        commentDto.setCreated(LocalDateTime.now());

        commentCreateDto = new CommentCreateDto(commentDto.getText());
    }

    @Test
    void addItem_ShouldReturnCreatedItem_WhenValidInput() throws Exception {

        when(userService.getUserById(1L)).thenReturn(userDto);
        when(itemService.createItem(eq(1L), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Item")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.available", is(true)));

        //verify(userService).getUserById(1L);
        verify(itemService).createItem(eq(1L), any(ItemDto.class));
    }

    @Test
    void addItem_ShouldReturnBadRequest_WhenMissingUserHeader() throws Exception {

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUserById(anyLong());
        verify(itemService, never()).createItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_WhenValidInput() throws Exception {
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(1L);
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setAvailable(false);

        when(itemService.updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class)))
                .thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Item")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.available", is(false)));

        verify(itemService).updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class));
    }

    @Test
    void updateItem_ShouldReturnNotFound_WhenItemNotExists() throws Exception {

        when(itemService.updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class)))
                .thenThrow(new ItemIdException(1L));

        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isNotFound());

        verify(itemService).updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class));
    }

    @Test
    void updateItem_ShouldReturnNotFound_WhenUserIsNotOwner() throws Exception {

        when(itemService.updateItem(eq(1L), eq(2L), any(ItemUpdateDto.class)))
                .thenThrow(new ItemIdException(1L));

        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isNotFound());

        verify(itemService).updateItem(eq(1L), eq(2L), any(ItemUpdateDto.class));
    }

    @Test
    void getItemById_ShouldReturnItem_WhenItemExists() throws Exception {

        when(itemService.getItemById(1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Item")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.available", is(true)));

        verify(itemService).getItemById(1L);
    }

    @Test
    void getItemById_ShouldReturnNotFound_WhenItemNotExists() throws Exception {

        when(itemService.getItemById(1L)).thenThrow(new ItemIdException(1L));

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isNotFound());

        verify(itemService).getItemById(1L);
    }

    @Test
    void getAllItemsByOwner_ShouldReturnItemsList() throws Exception {

        List<ItemDto> items = List.of(itemDto);
        when(itemService.getAllItemsByUserId(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Item")));

        verify(itemService).getAllItemsByUserId(1L);
    }

    @Test
    void getAllItemsByOwner_ShouldReturnEmptyList_WhenNoItems() throws Exception {

        when(itemService.getAllItemsByUserId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService).getAllItemsByUserId(1L);
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() throws Exception {

        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItems("test")).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Item")));

        verify(itemService).searchItems("test");
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenNoMatches() throws Exception {

        when(itemService.searchItems("nonexistent")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService).searchItems("nonexistent");
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsEmpty() throws Exception {

        when(itemService.searchItems("")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService).searchItems("");
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenMissingUserHeader() throws Exception {

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createComment(anyLong(), anyLong(), any(CommentCreateDto.class));
    }

    @Test
    void getAllItemsByOwner_ShouldReturnBadRequest_WhenMissingUserHeader() throws Exception {

        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItemsByUserId(anyLong());
    }

    @Test
    void updateItem_ShouldReturnBadRequest_WhenInvalidItemId() throws Exception {

        mockMvc.perform(patch("/items/invalid")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class));
    }

    @Test
    void getItemById_ShouldReturnBadRequest_WhenInvalidItemId() throws Exception {

        mockMvc.perform(get("/items/invalid"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemById(anyLong());
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenInvalidItemId() throws Exception {

        mockMvc.perform(post("/items/invalid/comment")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createComment(anyLong(), anyLong(), any(CommentCreateDto.class));
    }
}
