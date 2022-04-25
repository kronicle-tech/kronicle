package tech.kronicle.sdk.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TagOrStringTest {

    private final ObjectMapper objectMapper = new JsonMapper();

    @SneakyThrows
    @Test
    public void typeShouldDeserializeFromJsonString() {
        // Given
        String rawJson = "\"test-tag-key\"";

        // When
        TagOrString returnValue = objectMapper.readValue(rawJson, TagOrString.class);

        // Then
        assertThat(returnValue).isInstanceOf(Tag.class);
        assertThat(returnValue).isEqualTo(
                Tag.builder()
                        .key("test-tag-key")
                        .build()
        );
    }

    @SneakyThrows
    @Test
    public void typeShouldDeserializeFromJsonObject() {
        // Given
        String rawJson = "{\"key\":\"test-tag-key\",\"value\":\"test-tag-value\"}";

        // When
        TagOrString returnValue = objectMapper.readValue(rawJson, TagOrString.class);

        // Then
        assertThat(returnValue).isInstanceOf(Tag.class);
        assertThat(returnValue).isEqualTo(
                Tag.builder()
                        .key("test-tag-key")
                        .value("test-tag-value")
                        .build()
        );
    }
}
