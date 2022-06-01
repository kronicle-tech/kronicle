package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SoftwareRepositoriesStateTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        SoftwareRepositoriesState returnValue = new ObjectMapper().readValue(json, SoftwareRepositoriesState.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeSoftwareRepositoriesAnUnmodifiableList() {
        // Given
        SoftwareRepositoriesState underTest = SoftwareRepositoriesState.builder().softwareRepositories(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSoftwareRepositories().add(
                SoftwareRepository.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
