package tech.kronicle.plugins.aws.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class AwsConfigTest {

    @Test
    public void constructorShouldMakeProfilesAnUnmodifiableList() {
        // Given
        AwsConfig underTest = new AwsConfig(
                new ArrayList<>(),
                null,
                null,
                null,
                null
        );

        // When
        Throwable thrown = catchThrowable(() -> underTest.getProfiles().add(
                new AwsProfileConfig(null, null, null, null)
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
