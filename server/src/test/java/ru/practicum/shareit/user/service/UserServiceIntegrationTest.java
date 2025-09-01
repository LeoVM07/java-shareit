package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserEmailException;
import ru.practicum.shareit.exception.UserIdException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;

    private UserDto testUserDto;
    private UserDto secondUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("Integration Test User");
        testUserDto.setEmail("integration@test.com");

        secondUserDto = new UserDto();
        secondUserDto.setId(2L);
        secondUserDto.setName("Second User");
        secondUserDto.setEmail("second@test.com");
    }

    @Test
    void shouldCreateUserAndPersistToDatabase() {
        UserDto createdUser = userService.createUser(testUserDto);

        assertNotNull(createdUser.getId());
        assertEquals(testUserDto.getName(), createdUser.getName());
        assertEquals(testUserDto.getEmail(), createdUser.getEmail());

        User persistedUser = userRepository.findById(createdUser.getId()).orElse(null);
        assertNotNull(persistedUser);
        assertEquals(createdUser.getName(), persistedUser.getName());
        assertEquals(createdUser.getEmail(), persistedUser.getEmail());
    }

    @Test
    @DisplayName("Should prevent creating users with duplicate email")
    void shouldPreventCreatingUsersWithDuplicateEmail() {
        userService.createUser(testUserDto);

        UserDto duplicateEmailUser = new UserDto();
        duplicateEmailUser.setName("Different Name");
        duplicateEmailUser.setEmail(testUserDto.getEmail()); // Same email

        UserEmailException exception = assertThrows(UserEmailException.class,
                () -> userService.createUser(duplicateEmailUser));

        assertTrue(exception.getMessage().contains(testUserDto.getEmail()));
        assertTrue(exception.getMessage().contains("уже используется"));

        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());
    }

    @Test
    @Transactional
    void shouldUpdateUserAndPersistChangesToDatabase() {
        UserDto createdUser = userService.createUser(testUserDto);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Updated Integration User");
        updateDto.setEmail("updated@integration.com");

        UserDto updatedUser = userService.updateUser(createdUser.getId(), updateDto);

        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals(updateDto.getName(), updatedUser.getName());
        assertEquals(updateDto.getEmail(), updatedUser.getEmail());

        User persistedUser = userRepository.findById(updatedUser.getId()).orElse(null);
        assertNotNull(persistedUser);
        assertEquals(updateDto.getName(), persistedUser.getName());
        assertEquals(updateDto.getEmail(), persistedUser.getEmail());
    }

    @Test
    @Transactional
    void shouldPreventUpdatingToExistingEmail() {
        UserDto firstUser = userService.createUser(testUserDto);
        UserDto secondUser = userService.createUser(secondUserDto);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail(firstUser.getEmail());

        UserEmailException exception = assertThrows(UserEmailException.class,
                () -> userService.updateUser(secondUser.getId(), updateDto));

        assertTrue(exception.getMessage().contains(firstUser.getEmail()));
        assertTrue(exception.getMessage().contains("уже используется"));

        User persistedSecondUser = userRepository.findById(secondUser.getId()).orElse(null);
        assertNotNull(persistedSecondUser);
        assertEquals(secondUser.getEmail(), persistedSecondUser.getEmail());
    }

    @Test
    void shouldRetrieveUserByIdFromDatabase() {
        UserDto createdUser = userService.createUser(testUserDto);

        UserDto retrievedUser = userService.getUserById(createdUser.getId());

        assertNotNull(retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals(createdUser.getName(), retrievedUser.getName());
        assertEquals(createdUser.getEmail(), retrievedUser.getEmail());
    }

    @Test
    void shouldThrowNotFoundExceptionForNonExistentUser() {
        Long nonExistentId = 999L;

        UserIdException exception = assertThrows(UserIdException.class,
                () -> userService.getUserById(nonExistentId));

        assertTrue(exception.getMessage().contains("Пользователь"));
        assertTrue(exception.getMessage().contains(nonExistentId.toString()));
    }


    @Test
    void shouldDeleteUserFromDatabase() {
        UserDto createdUser = userService.createUser(testUserDto);
        Long userId = createdUser.getId();

        assertTrue(userRepository.existsById(userId));

        userService.deleteUser(userId);

        assertFalse(userRepository.existsById(userId));
        assertEquals(0, userRepository.findAll().size());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentUser() {
        Long nonExistentId = 999L;

        UserIdException exception = assertThrows(UserIdException.class,
                () -> userService.deleteUser(nonExistentId));

        assertTrue(exception.getMessage().contains("Пользователь"));
        assertTrue(exception.getMessage().contains(nonExistentId.toString()));
    }

    @Test
    @Transactional
    void shouldHandleComplexUserOperationsInSequence() {
        UserDto firstUser = userService.createUser(testUserDto);
        assertNotNull(firstUser.getId());

        UserDto secondUser = userService.createUser(secondUserDto);
        assertNotNull(secondUser.getId());

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Updated First User");
        updateDto.setEmail("updated.first@test.com");

        UserDto updatedFirstUser = userService.updateUser(firstUser.getId(), updateDto);
        assertEquals(updateDto.getName(), updatedFirstUser.getName());
        assertEquals(updateDto.getEmail(), updatedFirstUser.getEmail());

        userService.deleteUser(secondUser.getId());

        User persistedUser = userRepository.findById(updatedFirstUser.getId()).orElse(null);
        assertNotNull(persistedUser);
        assertEquals(updateDto.getName(), persistedUser.getName());
        assertEquals(updateDto.getEmail(), persistedUser.getEmail());
    }
}
