package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.pluginapi.finders.DiagramFinder;
import tech.kronicle.pluginapi.finders.Finder;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.service.services.testutils.FakePluginManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FinderExtensionRegistryTest {

    @Test
    public void getComponentFindersShouldReturnTheComponentFinders() {
        // Given
        TestComponentFinder componentFinder1 = new TestComponentFinder();
        TestComponentFinder componentFinder2 = new TestComponentFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(componentFinder1, componentFinder2));

        // When
        List<ComponentFinder> returnValue = underTest.getComponentFinders();

        // Then
        assertThat(returnValue).containsExactly(componentFinder1, componentFinder2);
    }

    @Test
    public void getComponentFindersShouldIgnoreOtherTypesOfFinder() {
        // Given
        TestOtherFinder otherFinder1 = new TestOtherFinder();
        TestComponentFinder componentFinder1 = new TestComponentFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(otherFinder1, componentFinder1));

        // When
        List<ComponentFinder> returnValue = underTest.getComponentFinders();

        // Then
        assertThat(returnValue).containsExactly(componentFinder1);
    }

    @Test
    public void getTracingDataFindersShouldReturnTheTracingDataFinders() {
        // Given
        TestTracingDataFinder tracingDataFinder1 = new TestTracingDataFinder();
        TestTracingDataFinder tracingDataFinder2 = new TestTracingDataFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(tracingDataFinder1, tracingDataFinder2));

        // When
        List<TracingDataFinder> returnValue = underTest.getTracingDataFinders();

        // Then
        assertThat(returnValue).containsExactly(tracingDataFinder1, tracingDataFinder2);
    }

    @Test
    public void getTracingDataFindersShouldIgnoreOtherTypesOfFinder() {
        // Given
        TestOtherFinder otherFinder1 = new TestOtherFinder();
        TestTracingDataFinder tracingDataFinder1 = new TestTracingDataFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(otherFinder1, tracingDataFinder1));

        // When
        List<TracingDataFinder> returnValue = underTest.getTracingDataFinders();

        // Then
        assertThat(returnValue).containsExactly(tracingDataFinder1);
    }

    @Test
    public void getRepoFindersShouldReturnTheRepoFinders() {
        // Given
        TestRepoFinder repoFinder1 = new TestRepoFinder();
        TestRepoFinder repoFinder2 = new TestRepoFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(repoFinder1, repoFinder2));

        // When
        List<RepoFinder> returnValue = underTest.getRepoFinders();

        // Then
        assertThat(returnValue).containsExactly(repoFinder1, repoFinder2);
    }

    @Test
    public void getRepoFindersShouldIgnoreOtherTypesOfFinder() {
        // Given
        TestOtherFinder otherFinder1 = new TestOtherFinder();
        TestRepoFinder repoFinder1 = new TestRepoFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(otherFinder1, repoFinder1));

        // When
        List<RepoFinder> returnValue = underTest.getRepoFinders();

        // Then
        assertThat(returnValue).containsExactly(repoFinder1);
    }

    @Test
    public void getDiagramFindersShouldReturnTheDiagramFinders() {
        // Given
        TestDiagramFinder diagramFinder1 = new TestDiagramFinder();
        TestDiagramFinder diagramFinder2 = new TestDiagramFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(diagramFinder1, diagramFinder2));

        // When
        List<DiagramFinder> returnValue = underTest.getDiagramFinders();

        // Then
        assertThat(returnValue).containsExactly(diagramFinder1, diagramFinder2);
    }

    @Test
    public void getDiagramFindersShouldIgnoreOtherTypesOfFinder() {
        // Given
        TestOtherFinder otherFinder1 = new TestOtherFinder();
        TestDiagramFinder diagramFinder1 = new TestDiagramFinder();
        FinderExtensionRegistry underTest = createUnderTest(List.of(otherFinder1, diagramFinder1));

        // When
        List<DiagramFinder> returnValue = underTest.getDiagramFinders();

        // Then
        assertThat(returnValue).containsExactly(diagramFinder1);
    }

    private FinderExtensionRegistry createUnderTest(List<Finder> finders) {
        return new FinderExtensionRegistry(new FakePluginManager<>(finders, Finder.class));
    }

    private static class TestComponentFinder extends ComponentFinder {
        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<ComponentsAndDiagrams, Void> find(ComponentMetadata componentMetadata) {
            return null;
        }
    }

    private static class TestTracingDataFinder extends TracingDataFinder {
        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<List<TracingData>, Void> find(ComponentMetadata componentMetadata) {
            return null;
        }
    }

    private static class TestRepoFinder extends RepoFinder {
        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<List<Repo>, Void> find(Void ignored) {
            return null;
        }
    }

    private static class TestDiagramFinder extends DiagramFinder {
        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<List<Diagram>, Void> find(ComponentMetadata componentMetadata) {
            return null;
        }
    }

    private static class TestOtherFinder extends Finder<ComponentMetadata, Object> {
        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Object, Void> find(ComponentMetadata componentMetadata) {
            return null;
        }
    }
}
