package tech.kronicle.plugins.doc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.doc.services.DocProcessor;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Doc;
import tech.kronicle.sdk.models.doc.DocState;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.doc.testutils.DocStateUtils.createDocStateWithDir;

@ExtendWith(MockitoExtension.class)
public class DocScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Mock
    private DocProcessor mockDocProcessor;

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        DocScanner underTest = createUnderTest();
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("doc");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        DocScanner underTest = createUnderTest();
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Saves documentation files from a component's Git repo and serves those files via Kronicle's UI");
    }

    @Test
    public void scanShouldReturnAnEmptyOutputWhenDocProcessorReturnsNoDocStates() {
        // Given
        List<Doc> docs = List.of(
                Doc.builder()
                        .dir("test-doc-dir")
                        .build()
        );
        Path codebaseDir = Path.of("test-codebase-dir");
        ComponentAndCodebase input = new ComponentAndCodebase(
                Component.builder()
                        .docs(docs)
                        .build(),
                new Codebase(null, codebaseDir)
        );
        DocScanner underTest = createUnderTest();
        List<DocState> docStates = List.of();
        when(mockDocProcessor.processDocs(codebaseDir, docs)).thenReturn(docStates);

        // When
        Output<Void, Component> returnValue = underTest.scan(input);

        // Then
        assertThat(returnValue).isEqualTo(Output.empty(CACHE_TTL));
    }

    @Test
    public void scanShouldReturnMultipleDocStatesWhenDocProcessorReturnsMultipleDocStates() {
        // Given
        List<Doc> docs = List.of(
                Doc.builder()
                        .dir("test-doc-dir")
                        .build()
        );
        Path codebaseDir = Path.of("test-codebase-dir");
        ComponentAndCodebase input = new ComponentAndCodebase(
                Component.builder()
                        .docs(docs)
                        .build(),
                new Codebase(null, codebaseDir)
        );
        DocScanner underTest = createUnderTest();
        List<DocState> docStates = List.of(
                createDocStateWithDir(1),
                createDocStateWithDir(2)
        );
        when(mockDocProcessor.processDocs(codebaseDir, docs)).thenReturn(docStates);

        // When
        Output<Void, Component> returnValue = underTest.scan(input);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<DocState> returnDocStates = getDocStates(returnValue);
        assertThat(returnDocStates).containsExactlyInAnyOrderElementsOf(docStates);
    }

    private DocScanner createUnderTest() {
        return new DocScanner(mockDocProcessor);
    }

    private List<DocState> getDocStates(Output<Void, Component> returnValue) {
        return getMutatedComponent(returnValue)
                .getStates(DocState.TYPE);
    }
}
