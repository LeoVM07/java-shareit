package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserEmailException;
import ru.practicum.shareit.exception.UserIdException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;
    private UserUpdateDto testUserUpdateDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@email.com");

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("Test User");
        testUserDto.setEmail("test@email.com");

        testUserUpdateDto = new UserUpdateDto();
        testUserUpdateDto.setName("Updated User");
        testUserUpdateDto.setEmail("updated@email.com");
    }

    @Nested
    class CreateUserTests {

        @Test
        void shouldCreateUser_WhenEmailIsUnique() {

            when(userRepository.existsByEmail(testUserDto.getEmail())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserDto result = userService.createUser(testUserDto);

            assertNotNull(result);
            assertEquals(testUserDto.getName(), result.getName());
            assertEquals(testUserDto.getEmail(), result.getEmail());
            assertEquals(testUser.getId(), result.getId());

            verify(userRepository).existsByEmail(testUserDto.getEmail());
            verify(userRepository).save(any(User.class));
        }

        @Test
        void shouldThrowConflictException_WhenEmailAlreadyExists() {

            when(userRepository.existsByEmail(testUserDto.getEmail())).thenReturn(true);

            UserEmailException exception = assertThrows(UserEmailException.class,
                    () -> userService.createUser(testUserDto));

            assertEquals("Email test@email.com уже используется другим пользователем!", exception.getMessage());

            verify(userRepository).existsByEmail(testUserDto.getEmail());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user name and email successfully")
        void shouldUpdateUserNameAndEmail_WhenDataIsValid() {

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("updated@email.com")).thenReturn(false);

            UserDto result = userService.updateUser(1L, testUserUpdateDto);

            assertNotNull(result);
            assertEquals("Updated User", result.getName());
            assertEquals("updated@email.com", result.getEmail());
            assertEquals(1L, result.getId());

            verify(userRepository).findById(1L);
            verify(userRepository).existsByEmail("updated@email.com");
        }

        @Test
        void shouldUpdateOnlyName_WhenEmailIsNull() {

            testUserUpdateDto.setEmail(null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            UserDto result = userService.updateUser(1L, testUserUpdateDto);

            assertNotNull(result);
            assertEquals("Updated User", result.getName());
            assertEquals("test@email.com", result.getEmail());

            verify(userRepository).findById(1L);
            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        void shouldUpdateOnlyEmail_WhenNameIsNull() {

            testUserUpdateDto.setName(null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("updated@email.com")).thenReturn(false);

            UserDto result = userService.updateUser(1L, testUserUpdateDto);

            assertNotNull(result);
            assertEquals("Test User", result.getName()); // Original name preserved
            assertEquals("updated@email.com", result.getEmail());

            verify(userRepository).findById(1L);
            verify(userRepository).existsByEmail("updated@email.com");
        }

        @Test
        void shouldNotCheckEmailExistence_WhenEmailIsUnchanged() {

            testUserUpdateDto.setEmail("test@email.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            UserDto result = userService.updateUser(1L, testUserUpdateDto);

            assertNotNull(result);
            assertEquals("Updated User", result.getName());
            assertEquals("test@email.com", result.getEmail());

            verify(userRepository).findById(1L);
            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Should throw NotFoundException when user does not exist")
        void shouldThrowNotFoundException_WhenUserDoesNotExist() {

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            UserIdException exception = assertThrows(UserIdException.class,
                    () -> userService.updateUser(999L, testUserUpdateDto));

            assertTrue(exception.getMessage().contains("Пользователь"));
            assertTrue(exception.getMessage().contains("999"));

            verify(userRepository).findById(999L);
            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        void shouldThrowConflictException_WhenNewEmailAlreadyExists() {

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("updated@email.com")).thenReturn(true);

            UserEmailException exception = assertThrows(UserEmailException.class,
                    () -> userService.updateUser(1L, testUserUpdateDto));

            assertEquals("Email updated@email.com уже используется другим пользователем!", exception.getMessage());

            verify(userRepository).findById(1L);
            verify(userRepository).existsByEmail("updated@email.com");
        }
    }

    @Nested
    class GetUserTests {

        @Test
        @DisplayName("Should return user by ID when user exists")
        void shouldReturnUser_WhenUserExists() {

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            UserDto result = userService.getUserById(1L);

            assertNotNull(result);
            assertEquals(testUser.getId(), result.getId());
            assertEquals(testUser.getName(), result.getName());
            assertEquals(testUser.getEmail(), result.getEmail());

            verify(userRepository).findById(1L);
        }

        @Test
        void shouldThrowNotFoundException_WhenUserDoesNotExist() {

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            UserIdException exception = assertThrows(UserIdException.class,
                    () -> userService.getUserById(999L));

            assertTrue(exception.getMessage().contains("Пользователь"));
            assertTrue(exception.getMessage().contains("999"));

            verify(userRepository).findById(999L);
        }
    }

    @Nested
    class DeleteUserTests {

        @Test
        void shouldDeleteUser_WhenUserExists() {

            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);

            assertDoesNotThrow(() -> userService.deleteUser(1L));

            verify(userRepository).existsById(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        void shouldThrowNotFoundException_WhenUserDoesNotExist() {

            when(userRepository.existsById(999L)).thenReturn(false);

            UserIdException exception = assertThrows(UserIdException.class,
                    () -> userService.deleteUser(999L));

            assertTrue(exception.getMessage().contains("Пользователь"));
            assertTrue(exception.getMessage().contains("999"));

            verify(userRepository).existsById(999L);
            verify(userRepository, never()).deleteById(anyLong());
        }
    }
}
