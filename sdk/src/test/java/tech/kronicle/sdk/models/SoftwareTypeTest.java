package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SoftwareTypeTest {

    @Test
    public void valueShouldReturnNameInKebabCase() {
        // When
        String returnValue = SoftwareType.GRADLE_PLUGIN.value();

        // Then
        assertThat(returnValue).isEqualTo("gradle-plugin");
    }
}
