package com.moneysupermarket.componentcatalog.service.scanners;

import com.moneysupermarket.componentcatalog.componentmetadata.models.ComponentMetadata;
import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
import com.moneysupermarket.componentcatalog.sdk.models.Summary;
import com.moneysupermarket.componentcatalog.service.scanners.models.Output;
import org.junit.jupiter.api.Test;

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
        public Output<Void> scan(TestInput input) {
            return Output.of(UnaryOperator.identity());
        }
    }
}
