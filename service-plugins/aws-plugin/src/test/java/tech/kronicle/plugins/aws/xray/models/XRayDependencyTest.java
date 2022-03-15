package tech.kronicle.plugins.aws.xray.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class XRayDependencyTest {

    @Test
    public void constructorShouldMakeSourceServiceNamesAnUnmodifiableList() {
        // Given
        XRayDependency underTest = new XRayDependency(new ArrayList<>(), null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSourceServiceNames().add(
                "test-source-service-name"
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTargetServiceNamesAnUnmodifiableList() {
        // Given
        XRayDependency underTest = new XRayDependency(null, new ArrayList<>());

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTargetServiceNames().add(
                "test-target-service-name"
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
