package tech.kronicle.plugins.javaimport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.javaimport.services.JavaImportFinder;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Import;
import tech.kronicle.sdk.models.ImportType;
import tech.kronicle.sdk.models.ImportsState;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;

public class JavaImportScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private JavaImportScanner underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new JavaImportScanner(createFileUtils(), new JavaImportFinder());
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("java-import");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a component's codebase and finds the names of all Java types imported by Java import statements");
    }

    @Test
    public void notesShouldReturnNull() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void scanShouldFindNothingInEmptyCodebase() {
        // Given
        Codebase testCodebase = new Codebase(getTestRepo(), getCodebaseDir("Empty"));

        // When
        Output<Void, Component> returnValue = underTest.scan(testCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<Import> imports = getImports(returnValue);
        assertThat(imports).isEmpty();
    }

    @Test
    public void scanShouldFindMultipleImportsInMultipleJavaFiles() {
        // Given
        Codebase testCodebase = new Codebase(getTestRepo(), getCodebaseDir("MultipleImportsInMultipleJavaFiles"));

        // When
        Output<Void, Component> returnValue = underTest.scan(testCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<Import> imports = getImports(returnValue);
        assertThat(imports).hasSize(3);
        Import importItem;
        importItem = imports.get(0);
        assertThat(importItem.getType()).isEqualTo(ImportType.JAVA);
        assertThat(importItem.getName()).isEqualTo("java.util.List");
        importItem = imports.get(1);
        assertThat(importItem.getType()).isEqualTo(ImportType.JAVA);
        assertThat(importItem.getName()).isEqualTo("java.util.Set");
        importItem = imports.get(2);
        assertThat(importItem.getType()).isEqualTo(ImportType.JAVA);
        assertThat(importItem.getName()).isEqualTo("java.util.stream.Stream");
        assertThat(returnValue.getErrors()).isEmpty();
    }

    @Test
    public void scanShouldDeduplicateDuplicateImports() {
        // Given
        Codebase testCodebase = new Codebase(getTestRepo(), getCodebaseDir("DuplicateImports"));

        // When
        Output<Void, Component> returnValue = underTest.scan(testCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<Import> imports = getImports(returnValue);
        assertThat(imports).hasSize(2);
        Import importItem;
        importItem = imports.get(0);
        assertThat(importItem.getType()).isEqualTo(ImportType.JAVA);
        assertThat(importItem.getName()).isEqualTo("java.util.List");
        importItem = imports.get(1);
        assertThat(importItem.getType()).isEqualTo(ImportType.JAVA);
        assertThat(importItem.getName()).isEqualTo("java.util.stream.Stream");
        assertThat(returnValue.getErrors()).isEmpty();
    }

    private List<Import> getImports(Output<Void, Component> returnValue) {
        ImportsState state = getMutatedComponent(returnValue).getState(ImportsState.TYPE);
        assertThat(state).isNotNull();
        return state.getImports();
    }
}
