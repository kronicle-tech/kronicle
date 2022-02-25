package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.pluginapi.finders.DependencyFinder;
import tech.kronicle.pluginapi.finders.Finder;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.service.services.testutils.FakePluginManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FinderExtensionRegistryTest {

    @Test
    public void getDependencyFindersShouldReturnTheDependencyFinders() {
        // Given
        TestDependencyFinder dependencyFinder1 = new TestDependencyFinder();
        TestDependencyFinder dependencyFinder2 = new TestDependencyFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(dependencyFinder1, dependencyFinder2));

        // When
        List<DependencyFinder> returnValue = underTest.getDependencyFinders();

        // Then
        assertThat(returnValue).containsExactly(dependencyFinder1, dependencyFinder2);
    }

    @Test
    public void getDependencyFindersShouldIgnoreOtherTypesOfFinder() {
        // Given
        TestOtherFinder otherFinder1 = new TestOtherFinder();
        TestDependencyFinder dependencyFinder1 = new TestDependencyFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(otherFinder1, dependencyFinder1));

        // When
        List<DependencyFinder> returnValue = underTest.getDependencyFinders();

        // Then
        assertThat(returnValue).containsExactly(dependencyFinder1);
    }

    private FinderExtensionRegistry createUnderTest(List<Finder> finders) {
        return new FinderExtensionRegistry(new FakePluginManager<>(finders, Finder.class));
    }

    private static class TestDependencyFinder extends DependencyFinder {
        @Override
        public String description() {
            return null;
        }

        @Override
        public List<Dependency> find(ComponentMetadata componentMetadata) {
            return null;
        }
    }

    private static class TestOtherFinder extends Finder<Object> {
        @Override
        public String description() {
            return null;
        }

        @Override
        public List<Object> find(ComponentMetadata componentMetadata) {
            return null;
        }
    }
}
