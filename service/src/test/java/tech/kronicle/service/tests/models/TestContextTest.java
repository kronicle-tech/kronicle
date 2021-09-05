package tech.kronicle.service.tests.models;

import tech.kronicle.sdk.models.Component;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestContextTest {

    @Test
    public void constructorShouldMakeComponentMapAnUnmodifiableMap() {
        // Given
        TestContext underTest = TestContext.builder().componentMap(new HashMap<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getComponentMap().put("test", Component.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}