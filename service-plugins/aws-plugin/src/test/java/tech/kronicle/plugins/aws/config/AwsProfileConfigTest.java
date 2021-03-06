package tech.kronicle.plugins.aws.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class AwsProfileConfigTest {

    @Test
    public void constructorShouldMakeRegionsAnUnmodifiableList() {
        // Given
        AwsProfileConfig underTest = new AwsProfileConfig(null, null, null, new ArrayList<>(), null, null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRegions().add("test-region"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
