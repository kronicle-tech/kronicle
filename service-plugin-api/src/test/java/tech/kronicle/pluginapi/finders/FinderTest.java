package tech.kronicle.pluginapi.finders;

import org.junit.jupiter.api.Test;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.pluginapi.finders.Finder;
import tech.kronicle.sdk.models.ObjectWithReference;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FinderTest {

    @Test
    public void idShouldReturnSimpleClassNameByDefaultWithSimpleClassNameConvertedToKebabCaseAndAnyFinderSuffixRemoved() {
        // Given
        ExampleFinder underTest = new ExampleFinder();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("example");
    }

    @Test
    public void notesShouldReturnNullByDefault() {
        // Given
        ExampleFinder underTest = new ExampleFinder();

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    private static class TestInput implements ObjectWithReference {

        @Override
        public String reference() {
            return "test_reference";
        }
    }

    private static class ExampleFinder extends Finder<TestInput> {

        @Override
        public String description() {
            return null;
        }

        @Override
        public List<TestInput> find(ComponentMetadata componentMetadata) {
            return null;
        }
    }
}
