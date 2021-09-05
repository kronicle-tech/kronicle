package tech.kronicle.service.services;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.service.scanners.CodebaseScanner;
import tech.kronicle.service.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.service.scanners.ComponentScanner;
import tech.kronicle.service.scanners.LateComponentScanner;
import tech.kronicle.service.scanners.RepoScanner;
import tech.kronicle.service.scanners.Scanner;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.ComponentAndCodebase;
import tech.kronicle.service.scanners.models.Output;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ScannerFinderTest {

    @Test
    public void getRepoScannerShouldReturnTheRepoScanner() {
        // Given
        TestRepoScanner repoScanner1 = new TestRepoScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(repoScanner1));

        // When
        RepoScanner returnValue = underTest.getRepoScanner();

        // Then
        assertThat(returnValue).isSameAs(repoScanner1);
    }

    @Test
    public void getRepoScannerShouldIgnoreOtherTypesOfScanner() {
        // Given
        TestComponentScanner componentScanner1 = new TestComponentScanner();
        TestRepoScanner repoScanner1 = new TestRepoScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(componentScanner1, repoScanner1));

        // When
        RepoScanner returnValue = underTest.getRepoScanner();

        // Then
        assertThat(returnValue).isSameAs(repoScanner1);
    }

    @Test
    public void getRepoScannerWhenThereIsNoRepoScannerShouldThrowARuntimeException() {
        // Given
        ScannerFinder underTest = new ScannerFinder(List.of());

        // When
        Throwable thrown = catchThrowable(underTest::getRepoScanner);

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasMessage("No RepoScanner has been configured");
    }

    @Test
    public void getRepoScannerWhenThereIsMoreThanOneRepoScannerShouldThrowARuntimeException() {
        // Given
        ScannerFinder underTest = new ScannerFinder(List.of(new TestRepoScanner(), new TestRepoScanner()));

        // When
        Throwable thrown = catchThrowable(underTest::getRepoScanner);

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasMessage("More than 1 RepoScanner has been configured");
    }

    @Test
    public void getComponentScannersShouldReturnTheComponentScanners() {
        // Given
        TestComponentScanner componentScanner1 = new TestComponentScanner();
        TestComponentScanner componentScanner2 = new TestComponentScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(componentScanner1, componentScanner2));

        // When
        List<ComponentScanner> returnValue = underTest.getComponentScanners();

        // Then
        assertThat(returnValue).containsExactly(componentScanner1, componentScanner2);
    }

    @Test
    public void getComponentScannersShouldIgnoreOtherTypesOfScanner() {
        // Given
        TestRepoScanner repoScanner1 = new TestRepoScanner();
        TestComponentScanner componentScanner1 = new TestComponentScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(repoScanner1, componentScanner1));

        // When
        List<ComponentScanner> returnValue = underTest.getComponentScanners();

        // Then
        assertThat(returnValue).containsExactly(componentScanner1);
    }

    @Test
    public void getCodebaseScannersShouldReturnTheCodebaseScanners() {
        // Given
        TestCodebaseScanner codebaseScanner1 = new TestCodebaseScanner();
        TestCodebaseScanner codebaseScanner2 = new TestCodebaseScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(codebaseScanner1, codebaseScanner2));

        // When
        List<CodebaseScanner> returnValue = underTest.getCodebaseScanners();

        // Then
        assertThat(returnValue).containsExactly(codebaseScanner1, codebaseScanner2);
    }

    @Test
    public void getCodebaseScannersShouldIgnoreOtherTypesOfScanner() {
        // Given
        TestRepoScanner repoScanner1 = new TestRepoScanner();
        TestCodebaseScanner codebaseScanner1 = new TestCodebaseScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(repoScanner1, codebaseScanner1));

        // When
        List<CodebaseScanner> returnValue = underTest.getCodebaseScanners();

        // Then
        assertThat(returnValue).containsExactly(codebaseScanner1);
    }

    @Test
    public void getComponentAndCodebaseScannersShouldReturnTheComponentAndCodebaseScanners() {
        // Given
        TestComponentAndCodebaseScanner componentAndCodebaseScanner1 = new TestComponentAndCodebaseScanner();
        TestComponentAndCodebaseScanner componentAndCodebaseScanner2 = new TestComponentAndCodebaseScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(componentAndCodebaseScanner1, componentAndCodebaseScanner2));

        // When
        List<ComponentAndCodebaseScanner> returnValue = underTest.getComponentAndCodebaseScanners();

        // Then
        assertThat(returnValue).containsExactly(componentAndCodebaseScanner1, componentAndCodebaseScanner2);
    }

    @Test
    public void getComponentAndCodebaseScannersShouldIgnoreOtherTypesOfScanner() {
        // Given
        TestRepoScanner repoScanner1 = new TestRepoScanner();
        TestComponentAndCodebaseScanner componentAndCodebaseScanner1 = new TestComponentAndCodebaseScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(repoScanner1, componentAndCodebaseScanner1));

        // When
        List<ComponentAndCodebaseScanner> returnValue = underTest.getComponentAndCodebaseScanners();

        // Then
        assertThat(returnValue).containsExactly(componentAndCodebaseScanner1);
    }
    
    @Test
    public void getLateComponentScannersShouldReturnTheLateComponentScanners() {
        // Given
        TestLateComponentScanner lateComponentScanner1 = new TestLateComponentScanner();
        TestLateComponentScanner lateComponentScanner2 = new TestLateComponentScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(lateComponentScanner1, lateComponentScanner2));

        // When
        List<LateComponentScanner> returnValue = underTest.getLateComponentScanners();

        // Then
        assertThat(returnValue).containsExactly(lateComponentScanner1, lateComponentScanner2);
    }

    @Test
    public void getLateComponentScannersShouldIgnoreOtherTypesOfScanner() {
        // Given
        TestRepoScanner repoScanner1 = new TestRepoScanner();
        TestLateComponentScanner lateComponentScanner1 = new TestLateComponentScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(repoScanner1, lateComponentScanner1));

        // When
        List<LateComponentScanner> returnValue = underTest.getLateComponentScanners();

        // Then
        assertThat(returnValue).containsExactly(lateComponentScanner1);
    }

    @Test
    public void getAllScannersShouldReturnAllTheScanners() {
        // Given
        TestRepoScanner repoScanner1 = new TestRepoScanner();
        TestComponentScanner componentScanner1 = new TestComponentScanner();
        TestComponentScanner componentScanner2 = new TestComponentScanner();
        TestCodebaseScanner codebaseScanner1 = new TestCodebaseScanner();
        TestCodebaseScanner codebaseScanner2 = new TestCodebaseScanner();
        TestComponentAndCodebaseScanner componentAndCodebaseScanner1 = new TestComponentAndCodebaseScanner();
        TestComponentAndCodebaseScanner componentAndCodebaseScanner2 = new TestComponentAndCodebaseScanner();
        TestLateComponentScanner lateComponentScanner1 = new TestLateComponentScanner();
        TestLateComponentScanner lateComponentScanner2 = new TestLateComponentScanner();
        List<Scanner<?, ?>> scanners = List.of(repoScanner1, componentScanner1, componentScanner2, codebaseScanner1, codebaseScanner2,
                componentAndCodebaseScanner1, componentAndCodebaseScanner2, lateComponentScanner1, lateComponentScanner2);
        ScannerFinder underTest = new ScannerFinder(scanners);

        // When
        List<Scanner<?, ?>> returnValue = underTest.getAllScanners();

        // Then
        assertThat(returnValue).isEqualTo(scanners);
    }

    @Test
    public void getScannerShouldReturnAScannerWithMatchingId() {
        // Given
        TestComponentScanner componentScanner1 = new TestComponentScanner();
        TestComponentScanner componentScanner2 = new TestComponentScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(componentScanner1, componentScanner2));

        // When
        Scanner<?, ?> returnValue = underTest.getScanner(componentScanner1.id());

        // Then
        assertThat(returnValue).isSameAs(componentScanner1);
    }

    @Test
    public void getScannerShouldReturnNullWhenIdDoesNotMatchAScanner() {
        // Given
        String componentId = "unknown";
        TestComponentScanner componentScanner1 = new TestComponentScanner();
        ScannerFinder underTest = new ScannerFinder(List.of(componentScanner1));

        // When
        Scanner<?, ?> returnValue = underTest.getScanner(componentId);

        // Then
        assertThat(returnValue).isNull();
    }

    private static class TestRepoScanner extends RepoScanner {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Codebase> scan(Repo input) {
            return Output.of(UnaryOperator.identity(), new Codebase(new Repo("https://example.com/example.git"), Path.of("test-codebase-dir")));
        }
    }

    private static class TestComponentScanner extends ComponentScanner {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void> scan(Component input) {
            return Output.of(UnaryOperator.identity());
        }
    }

    private static class TestCodebaseScanner extends CodebaseScanner {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void> scan(Codebase input) {
            return Output.of(UnaryOperator.identity());
        }
    }

    private static class TestComponentAndCodebaseScanner extends ComponentAndCodebaseScanner {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void> scan(ComponentAndCodebase input) {
            return Output.of(UnaryOperator.identity());
        }
    }

    private static class TestLateComponentScanner extends LateComponentScanner {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void> scan(Component input) {
            return Output.of(UnaryOperator.identity());
        }
    }
}
