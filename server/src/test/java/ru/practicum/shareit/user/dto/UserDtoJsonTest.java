package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("UserDto JSON Serialization Tests")
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void shouldSerializeUserDtoToJson() throws IOException {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@email.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@email.com");

        assertThat(result).isEqualToJson("{\"id\": 1, \"name\": \"Test User\", \"email\": \"test@email.com\"}");
    }

    @Test
    void shouldDeserializeJsonToUserDto() throws IOException {

        String jsonContent = "{\"id\": 1, \"name\": \"Test User\", \"email\": \"test@email.com\"}";

        UserDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void shouldHandleNullValuesInSerialization() throws IOException {

        UserDto userDto = new UserDto();
        userDto.setId(null);
        userDto.setName(null);
        userDto.setEmail(null);

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }

    @Test
    void shouldHandleNullValuesInDeserialization() throws IOException {

        String jsonContent = "{\"id\": null, \"name\": null, \"email\": null}";

        UserDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void shouldHandleMissingFieldsInDeserialization() throws IOException {

        String jsonContent = "{\"name\": \"Test User\"}";

        UserDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void shouldHandleEmptyJsonObject() throws IOException {

        String jsonContent = "{}";

        UserDto result = json.parse(jsonContent).getObject();
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void shouldSerializeUserDtoWithOnlyId() throws IOException {

        UserDto userDto = new UserDto();
        userDto.setId(42L);

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(42);
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }

    @Test
    void shouldHandleSpecialCharactersInNameAndEmail() throws IOException {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User with emojis 😊");
        userDto.setEmail("test+tag@example-domain.co.uk");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Test User with emojis 😊");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("test+tag@example-domain.co.uk");
    }

    @Test
    void shouldDeserializeSpecialCharactersCorrectly() throws IOException {

        String jsonContent = "{\"id\": 1, \"name\": \"Test User with emojis 😊\", \"email\": \"test+tag@example-domain.co.uk\"}";

        UserDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("Test User with emojis 😊");
        assertThat(result.getEmail()).isEqualTo("test+tag@example-domain.co.uk");
    }
}
