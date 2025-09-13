package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserEmailException;
import ru.practicum.shareit.exception.UserIdException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto testUserDto;
    private UserUpdateDto testUserUpdateDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("Test User");
        testUserDto.setEmail("test@email.com");

        testUserUpdateDto = new UserUpdateDto();
        testUserUpdateDto.setName("Updated User");
        testUserUpdateDto.setEmail("updated@email.com");
    }

    @Nested
    class CreateUserEndpointTests {

        @Test
        void shouldCreateUserSuccessfully() throws Exception {

            UserDto inputDto = new UserDto();
            inputDto.setName("Test User");
            inputDto.setEmail("test@email.com");

            when(userService.createUser(any(UserDto.class))).thenReturn(testUserDto);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Test User")))
                    .andExpect(jsonPath("$.email", is("test@email.com")));
        }

        @Test
        void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

            UserDto inputDto = new UserDto();
            inputDto.setName("Test User");
            inputDto.setEmail("test@email.com");

            when(userService.createUser(any(UserDto.class)))
                    .thenThrow(new UserEmailException(inputDto.getEmail()));

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error", is("Ошибка в email пользователя")));
        }

        @Nested
        class UpdateUserEndpointTests {

            @Test
            @DisplayName("Should update user successfully")
            void shouldUpdateUserSuccessfully() throws Exception {

                UserDto updatedUserDto = new UserDto();
                updatedUserDto.setId(1L);
                updatedUserDto.setName("Updated User");
                updatedUserDto.setEmail("updated@email.com");

                when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(updatedUserDto);

                mockMvc.perform(patch("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUserUpdateDto)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id", is(1)))
                        .andExpect(jsonPath("$.name", is("Updated User")))
                        .andExpect(jsonPath("$.email", is("updated@email.com")));
            }

            @Test
            void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

                when(userService.updateUser(eq(999L), any(UserUpdateDto.class)))
                        .thenThrow(new UserIdException(999L));

                mockMvc.perform(patch("/users/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUserUpdateDto)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.error", is("Ошибка в id пользователя")));
            }

            @Test
            void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

                when(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                        .thenThrow(new UserEmailException("updated@email.com"));

                mockMvc.perform(patch("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUserUpdateDto)))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.error", is("Ошибка в email пользователя")));
            }

            @Test
            void shouldUpdateUserWithPartialData() throws Exception {

                UserUpdateDto partialUpdate = new UserUpdateDto();
                partialUpdate.setName("Only Name Updated");

                UserDto updatedUserDto = new UserDto();
                updatedUserDto.setId(1L);
                updatedUserDto.setName("Only Name Updated");
                updatedUserDto.setEmail("test@email.com");

                when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(updatedUserDto);

                mockMvc.perform(patch("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(partialUpdate)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(1)))
                        .andExpect(jsonPath("$.name", is("Only Name Updated")))
                        .andExpect(jsonPath("$.email", is("test@email.com")));
            }
        }

        @Nested
        class GetUserByIdEndpointTests {

            @Test
            void shouldReturnUserWhenUserExists() throws Exception {

                when(userService.getUserById(1L)).thenReturn(testUserDto);

                mockMvc.perform(get("/users/1"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id", is(1)))
                        .andExpect(jsonPath("$.name", is("Test User")))
                        .andExpect(jsonPath("$.email", is("test@email.com")));
            }

            @Test
            void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

                when(userService.getUserById(999L))
                        .thenThrow(new UserIdException(999L));

                mockMvc.perform(get("/users/999"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.error", is("Ошибка в id пользователя")));
            }
        }
    }

    @Nested
    class DeleteUserEndpointTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() throws Exception {

            doNothing().when(userService).deleteUser(1L);

            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

            doThrow(new UserIdException(999L))
                    .when(userService).deleteUser(999L);

            mockMvc.perform(delete("/users/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is("Ошибка в id пользователя")));
        }

    }

    @Nested
    class InputValidationTests {

        @Test
        void shouldHandleMalformedJson() throws Exception {

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleEmptyRequestBody() throws Exception {

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleMissingContentType() throws Exception {

            UserDto inputDto = new UserDto();
            inputDto.setName("Test User");
            inputDto.setEmail("test@email.com");

            mockMvc.perform(post("/users")
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

}