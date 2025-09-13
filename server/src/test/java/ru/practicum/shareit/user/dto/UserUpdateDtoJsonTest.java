package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserUpdateDtoJsonTest {

    @Autowired
    private JacksonTester<UserUpdateDto> json;

    @Test
    void shouldSerializeUserUpdateDtoToJson() throws IOException {

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Updated User");
        userUpdateDto.setEmail("updated@email.com");

        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Updated User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("updated@email.com");

        assertThat(result).isEqualToJson("{\"name\": \"Updated User\", \"email\": \"updated@email.com\"}");
    }

    @Test
    void shouldDeserializeJsonToUserUpdateDto() throws IOException {

        String jsonContent = "{\"name\": \"Updated User\", \"email\": \"updated@email.com\"}";


        UserUpdateDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("Updated User");
        assertThat(result.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void shouldHandlePartialUpdateWithOnlyName() throws IOException {

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Only Name Updated");
        userUpdateDto.setEmail(null);

        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Only Name Updated");
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }

    @Test
    void shouldHandlePartialUpdateWithOnlyEmail() throws IOException {

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(null);
        userUpdateDto.setEmail("only.email@updated.com");

        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("only.email@updated.com");
    }

    @Test
    void shouldDeserializePartialJsonWithOnlyName() throws IOException {

        String jsonContent = "{\"name\": \"Only Name Updated\"}";

        UserUpdateDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("Only Name Updated");
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void shouldDeserializePartialJsonWithOnlyEmail() throws IOException {

        String jsonContent = "{\"email\": \"only.email@updated.com\"}";

        UserUpdateDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isEqualTo("only.email@updated.com");
    }

    @Test
    void shouldHandleEmptyJsonObject() throws IOException {

        String jsonContent = "{}";

        UserUpdateDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void shouldHandleNullValuesInSerialization() throws IOException {

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(null);
        userUpdateDto.setEmail(null);

        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }

    @Test
    void shouldHandleNullValuesInDeserialization() throws IOException {

        String jsonContent = "{\"name\": null, \"email\": null}";

        UserUpdateDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void shouldHandleWhitespaceOnlyValues() throws IOException {

        String jsonContent = "{\"name\": \"   \", \"email\": \"\\t\\n \"}";

        UserUpdateDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("   ");
        assertThat(result.getEmail()).isEqualTo("\t\n ");
    }

    @Test
    void shouldHandleEmptyStringValues() throws IOException {

        String jsonContent = "{\"name\": \"\", \"email\": \"\"}";

        UserUpdateDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("");
        assertThat(result.getEmail()).isEqualTo("");
    }

    @Test
    void shouldHandleLongValues() throws IOException {

        String longName = "A".repeat(500);
        String longEmail = "test" + "a".repeat(500) + "@email.com";

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(longName);
        userUpdateDto.setEmail(longEmail);

        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(longName);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(longEmail);
    }

    @Test
    void shouldHandleSpecialCharactersAndUnicode() throws IOException {

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Тест Пользователь with émojis 😊 and quotes \"'`");
        userUpdateDto.setEmail("test+special.chars@пример.рф");

        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Тест Пользователь with émojis 😊 and quotes \"'`");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("test+special.chars@пример.рф");
    }
}
