package tech.kronicle.pluginapi.scanners;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.Summary;

import java.time.Duration;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

public class ScannerTest {

    @Test
    public void idShouldReturnSimpleClassNameByDefaultWithSimpleClassNameConvertedToKebabCaseAndAnyScannerSuffixRemoved() {
        // Given
        ExampleScanner underTest = new ExampleScanner();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("example");
    }

    @Test
    public void notesShouldReturnNullByDefault() {
        // Given
        ExampleScanner underTest = new ExampleScanner();

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void refreshShouldDoNothingByDefault() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        ExampleScanner underTest = new ExampleScanner();

        // When
        underTest.refresh(componentMetadata);
    }

    @Test
    public void transformSummaryShouldDoNothingByDefault() {
        // Given
        ExampleScanner underTest = new ExampleScanner();
        Summary summary = Summary.EMPTY;

        // When
        Summary returnValue = underTest.transformSummary(summary);

        // Then
        assertThat(returnValue).isSameAs(summary);
    }

    private static class TestInput implements ObjectWithReference {

        @Override
        public String reference() {
            return "test_reference";
        }
    }

    private static class ExampleScanner extends Scanner<TestInput, Void> {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(TestInput input) {
            return Output.ofTransformer(null, Duration.ZERO);
        }
    }
}
