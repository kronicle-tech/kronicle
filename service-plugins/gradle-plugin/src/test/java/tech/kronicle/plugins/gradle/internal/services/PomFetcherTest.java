package tech.kronicle.plugins.gradle.internal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.plugins.gradle.internal.constants.MavenPackagings;
import tech.kronicle.plugins.gradle.internal.models.Pom;
import tech.kronicle.plugins.gradle.internal.models.PomOutcome;
import tech.kronicle.plugins.gradle.internal.utils.ArtifactUtils;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PomFetcherTest {

    private static final String POM_ARTIFACT_COORDINATES = "com.example:example:1.2.3";

    private static final Set<SoftwareRepository> SOFTWARE_REPOSITORIES = Set.of(
            SoftwareRepository.builder().url("https://example.com/test-1").build(),
            SoftwareRepository.builder().url("https://example.com/test-2").build());

    private static final String POM_URL = "https://example.com/test-1/com/example/example/1.2.3/example-1.2.3.pom";

    private static final String POM_XML = "<project>\n"
            + "  <modelVersion>4.0.0</modelVersion>\n"
            + "  <groupId>com.example</groupId>\n"
            + "  <artifactId>example</artifactId>\n"
            + "  <version>1.2.3</version>\n"
            + "</project>";

    private static final String POM_JSON = "{\n"
            + "  \"artifactCoordinates\": \"com.example:example:1.2.3\",\n"
            + "  \"properties\": {\n"
            + "    \"project.groupId\": \"com.example\",\n"
            + "    \"project.version\": \"1.2.3\"\n"
            + "  },\n"
            + "  \"transitiveArtifactCoordinates\": [],\n"
            + "  \"dependencyManagementDependencies\": [],\n"
            + "  \"dependencies\": []\n"
            + "}";

    private static final String JAR_URL = "https://example.com/test-1/com/example/example/1.2.3/example-1.2.3.jar";

    private static final Pom POM = Pom.builder()
            .artifactCoordinates(POM_ARTIFACT_COORDINATES)
            .properties(Map.ofEntries(
                    Map.entry("project.groupId", "com.example"),
                    Map.entry("project.version", "1.2.3")))
            .build();

    private PomFetcher underTest;
    @Mock
    private MavenRepositoryFileDownloader mockMavenRepositoryFileDownloader;
    @Mock
    private PomCache mockPomCache;
    @Mock
    private PropertyExpander mockPropertyExpander;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ArtifactUtils artifactUtils = new ArtifactUtils();

    @BeforeEach
    public void beforeEach() throws JAXBException {
        underTest = new PomFetcher(mockMavenRepositoryFileDownloader, mockPomCache, mockPropertyExpander, objectMapper, artifactUtils);
        underTest.initialize();
    }

    @Test
    public void fetchPomShouldDownloadAndParseAPom() {
        // Given
        when(mockMavenRepositoryFileDownloader.downloadArtifact(POM_ARTIFACT_COORDINATES, MavenPackagings.POM, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(POM_URL, true, POM_XML, List.of()));

        // When
        PomOutcome returnValue = underTest.fetchPom(POM_ARTIFACT_COORDINATES, SOFTWARE_REPOSITORIES);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.isJarOnly()).isFalse();
        assertThat(returnValue.getPom()).isEqualTo(POM);
    }

    @Test
    public void fetchPomShouldUsePomJsonFromPomCacheWhenAvailable() {
        // Given
        when(mockMavenRepositoryFileDownloader.downloadArtifact(POM_ARTIFACT_COORDINATES, MavenPackagings.POM, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(POM_URL, true, POM_XML, List.of()));
        when(mockPomCache.get(POM_URL)).thenReturn(Optional.of(POM_JSON));

        // When
        PomOutcome returnValue = underTest.fetchPom(POM_ARTIFACT_COORDINATES, SOFTWARE_REPOSITORIES);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.isJarOnly()).isFalse();
        assertThat(returnValue.getPom()).isEqualTo(POM);
    }

    @Test
    public void fetchPomShouldThrowAnExceptionWhenPomDownloadFailsWithAnException() {
        // Given
        Exception downloadException = new Exception();
        when(mockMavenRepositoryFileDownloader.downloadArtifact(POM_ARTIFACT_COORDINATES, MavenPackagings.POM, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(POM_URL, false, null, List.of(downloadException)));

        // When
        Throwable thrown = catchThrowable(() -> underTest.fetchPom(POM_ARTIFACT_COORDINATES, SOFTWARE_REPOSITORIES));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage("Error with retrieving POM artifact \"com.example:example:1.2.3\"");
        assertThat(thrown).hasCause(downloadException);
    }

    @Test
    public void fetchPomShouldThrowAnExceptionWhenPomDoesNotExistAndJarExistsCheckFailsWithAnException() {
        // Given
        Exception existsException = new Exception();
        when(mockMavenRepositoryFileDownloader.downloadArtifact(POM_ARTIFACT_COORDINATES, MavenPackagings.POM, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(POM_URL, false, null, List.of()));
        when(mockMavenRepositoryFileDownloader.checkArtifactExists(POM_ARTIFACT_COORDINATES, MavenPackagings.JAR, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(JAR_URL, false, null, List.of(existsException)));

        // When
        Throwable thrown = catchThrowable(() -> underTest.fetchPom(POM_ARTIFACT_COORDINATES, SOFTWARE_REPOSITORIES));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage("Error with checking existence of JAR artifact \"com.example:example:1.2.3\"");
        assertThat(thrown).hasCause(existsException);
    }

    @Test
    public void fetchPomShouldReturnAnOutcomeWithJarOnlyTrueWhenPomDoesNotExistButJarDoesExist() {
        // Given
        when(mockMavenRepositoryFileDownloader.downloadArtifact(POM_ARTIFACT_COORDINATES, MavenPackagings.POM, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(POM_URL, false, null, List.of()));
        when(mockMavenRepositoryFileDownloader.checkArtifactExists(POM_ARTIFACT_COORDINATES, MavenPackagings.JAR, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(JAR_URL, true, true, List.of()));

        // When
        PomOutcome returnValue = underTest.fetchPom(POM_ARTIFACT_COORDINATES, SOFTWARE_REPOSITORIES);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.isJarOnly()).isTrue();
        assertThat(returnValue.getPom()).isNull();
    }

    @Test
    public void fetchPomShouldThrowAnExceptionWhenPomDoesNotExistAndJarDoesNotExist() {
        // Given
        when(mockMavenRepositoryFileDownloader.downloadArtifact(POM_ARTIFACT_COORDINATES, MavenPackagings.POM, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(POM_URL, false, null, List.of()));
        when(mockMavenRepositoryFileDownloader.checkArtifactExists(POM_ARTIFACT_COORDINATES, MavenPackagings.JAR, SOFTWARE_REPOSITORIES))
                .thenReturn(new MavenRepositoryFileDownloader.MavenFileRequestOutcome<>(JAR_URL, true, false, List.of()));

        // When
        Throwable thrown = catchThrowable(() -> underTest.fetchPom(POM_ARTIFACT_COORDINATES, SOFTWARE_REPOSITORIES));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage("Could not retrieve POM artifact \"com.example:example:1.2.3\" from safe subset of configured repositories");
        assertThat(thrown).hasNoCause();
    }
}