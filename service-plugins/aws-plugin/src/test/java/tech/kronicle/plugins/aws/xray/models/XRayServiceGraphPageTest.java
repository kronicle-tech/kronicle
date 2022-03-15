package tech.kronicle.plugins.aws.xray.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class XRayServiceGraphPageTest {

    @Test
    public void constructorShouldMakeItemsAnUnmodifiableList() {
        // Given
        XRayServiceGraphPage underTest = new XRayServiceGraphPage(new ArrayList<>(), null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getItems().add(
                new XRayDependency(null, null)
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
