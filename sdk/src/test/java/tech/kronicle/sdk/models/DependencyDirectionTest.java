package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyDirectionTest {

    @Test
    public void valueShouldReturnNameInKebabCase() {
        // When
        String returnValue = DependencyDirection.OUTBOUND.value();

        // Then
        // TODO: Currently there are no multi-word entries in this enum. As soon as there are, this test should be updated to use one of them
        assertThat(returnValue).isEqualTo("outbound");
    }
}
