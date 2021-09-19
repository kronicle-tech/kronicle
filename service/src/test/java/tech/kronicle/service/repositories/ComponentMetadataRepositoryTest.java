package tech.kronicle.service.repositories;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Area;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Team;
import tech.kronicle.service.BaseTest;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.models.RepoDirAndGit;
import tech.kronicle.service.repofinders.RepoProvider;
import tech.kronicle.service.services.GitCloner;
import tech.kronicle.service.services.RepoProviderFinder;
import tech.kronicle.service.services.ValidatorService;
import tech.kronicle.service.testutils.LogCaptor;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.service.testutils.ValidatorServiceFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComponentMetadataRepositoryTest extends BaseTest {

    private static final String REPO_URL_1 = "https://example.com/repo-1.git";
    private static final String REPO_URL_2 = "https://example.com/repo-2.git";
    private static final String REPO_URL_3 = "https://example.com/repo-3.git";
    private static final String REPO_URL_4 = "https://example.com/repo-4.git";

    @Mock
    private RepoProviderFinder mockFinder;
    @Mock
    private GitCloner mockGitCloner;
    private final ValidatorService validatorService = ValidatorServiceFactory.createValidationService();
    private LogCaptor logCaptor;
    private ComponentMetadataRepository underTest;

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(ComponentMetadataRepository.class);
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
    }

    @Test
    public void getComponentMetadataShouldGetComponentMetadataFromMultipleRepos() throws IOException, GitAPIException, URISyntaxException {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo1")).build());
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo2")).build());
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_3)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo3")).build());
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_4)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo4")).build());
        mockRepoProviders(List.of(new ApiRepo(REPO_URL_1, true), new ApiRepo(REPO_URL_2, true)),
                List.of(new ApiRepo(REPO_URL_3, true), new ApiRepo(REPO_URL_4, true)));
        underTest = new ComponentMetadataRepository(mockFinder, mockGitCloner, new YAMLMapper(), validatorService);
        
        // When
        ComponentMetadata returnValue = underTest.getComponentMetadata();
        
        // Then
        assertThat(returnValue.getAreas()).hasSize(8);
        assertThat(returnValue.getAreas().stream().map(Area::getId).collect(Collectors.toList())).containsExactly(
                "test-area-1",
                "test-area-2",
                "test-area-3",
                "test-area-4",
                "test-area-5",
                "test-area-6",
                "test-area-7",
                "test-area-8");
        assertThat(returnValue.getTeams()).hasSize(8);
        assertThat(returnValue.getTeams().stream().map(Team::getId).collect(Collectors.toList())).containsExactly(
                "test-team-1",
                "test-team-2",
                "test-team-3",
                "test-team-4",
                "test-team-5",
                "test-team-6",
                "test-team-7",
                "test-team-8");
        assertThat(returnValue.getComponents()).hasSize(8);
        assertThat(returnValue.getComponents().stream().map(Component::getId).collect(Collectors.toList())).containsExactly(
                "test-component-1",
                "test-component-2",
                "test-component-3",
                "test-component-4",
                "test-component-5",
                "test-component-6",
                "test-component-7",
                "test-component-8");
    }

    @Test
    public void getComponentMetadataShouldIgnoreARepoWithNoComponentMetadataFile() throws IOException, GitAPIException, URISyntaxException {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo2")).build());
        mockRepoProviders(List.of(new ApiRepo(REPO_URL_1, false), new ApiRepo(REPO_URL_2, true)));
        underTest = new ComponentMetadataRepository(mockFinder, mockGitCloner, new YAMLMapper(), validatorService);

        // When
        ComponentMetadata returnValue = underTest.getComponentMetadata();

        // Then
        assertThat(returnValue.getAreas()).hasSize(2);
        assertThat(returnValue.getAreas().stream().map(Area::getId).collect(Collectors.toList())).containsExactly(
                "test-area-3",
                "test-area-4");
        assertThat(returnValue.getTeams()).hasSize(2);
        assertThat(returnValue.getTeams().stream().map(Team::getId).collect(Collectors.toList())).containsExactly(
                "test-team-3",
                "test-team-4");
        assertThat(returnValue.getComponents()).hasSize(2);
        assertThat(returnValue.getComponents().stream().map(Component::getId).collect(Collectors.toList())).containsExactly(
                "test-component-3",
                "test-component-4");
    }

    @Test
    public void getComponentMetadataShouldSkipARepoIfCloningOrPullingTheRepoFails() throws IOException, GitAPIException, URISyntaxException {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo1")).build());
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenThrow(new IOException("Test Exception"));
        mockRepoProviders(List.of(new ApiRepo(REPO_URL_1, true), new ApiRepo(REPO_URL_2, true)));
        underTest = new ComponentMetadataRepository(mockFinder, mockGitCloner, new YAMLMapper(), validatorService);

        // When
        ComponentMetadata returnValue = underTest.getComponentMetadata();

        // Then
        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(1);
        ILoggingEvent event;
        event = events.get(0);
        assertThat(event.getFormattedMessage()).isEqualTo("Could not read Component Metadata file from repo \"https://example.com/repo-2.git\"");
        assertThat(event.getThrowableProxy().getClassName()).isEqualTo("java.io.IOException");
        assertThat(event.getThrowableProxy().getMessage()).isEqualTo("Test Exception");

        assertThat(returnValue.getAreas()).hasSize(2);
        assertThat(returnValue.getAreas().stream().map(Area::getId).collect(Collectors.toList())).containsExactly(
                "test-area-1",
                "test-area-2");
        assertThat(returnValue.getTeams()).hasSize(2);
        assertThat(returnValue.getTeams().stream().map(Team::getId).collect(Collectors.toList())).containsExactly(
                "test-team-1",
                "test-team-2");
        assertThat(returnValue.getComponents()).hasSize(2);
        assertThat(returnValue.getComponents().stream().map(Component::getId).collect(Collectors.toList())).containsExactly(
                "test-component-1",
                "test-component-2");
    }

    @Test
    public void getComponentMetadataShouldSkipARepoIfItDoesNotAContainComponentMetadata() throws IOException, GitAPIException, URISyntaxException {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo1")).build());
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("RepoWithNoComponentMetadata")).build());
        mockRepoProviders(List.of(new ApiRepo(REPO_URL_1, true), new ApiRepo(REPO_URL_2, true)));
        underTest = new ComponentMetadataRepository(mockFinder, mockGitCloner, new YAMLMapper(), validatorService);

        // When
        ComponentMetadata returnValue = underTest.getComponentMetadata();

        // Then
        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(1);
        ILoggingEvent event;
        event = events.get(0);
        assertThat(event.getFormattedMessage()).isEqualTo("Could not read Component Metadata file from repo \"https://example.com/repo-2.git\"");
        assertThat(event.getThrowableProxy().getClassName()).isEqualTo("java.nio.file.NoSuchFileException");
        assertThat(event.getThrowableProxy().getMessage()).endsWith(
                "service/src/test/resources/tech/kronicle/service/repositories/ComponentMetadataRepositoryTest/RepoWithNoComponentMetadata/component-metadata.yaml");

        assertThat(returnValue.getAreas()).hasSize(2);
        assertThat(returnValue.getAreas().stream().map(Area::getId).collect(Collectors.toList())).containsExactly(
                "test-area-1",
                "test-area-2");
        assertThat(returnValue.getTeams()).hasSize(2);
        assertThat(returnValue.getTeams().stream().map(Team::getId).collect(Collectors.toList())).containsExactly(
                "test-team-1",
                "test-team-2");
        assertThat(returnValue.getComponents()).hasSize(2);
        assertThat(returnValue.getComponents().stream().map(Component::getId).collect(Collectors.toList())).containsExactly(
                "test-component-1",
                "test-component-2");
    }

    @Test
    public void getComponentMetadataShouldSkipARepoIfItContainsInvalidYaml() throws IOException, GitAPIException, URISyntaxException {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo1")).build());
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("RepoWithInvalidYaml")).build());
        mockRepoProviders(List.of(new ApiRepo(REPO_URL_1, true), new ApiRepo(REPO_URL_2, true)));
        underTest = new ComponentMetadataRepository(mockFinder, mockGitCloner, new YAMLMapper(), validatorService);

        // When
        ComponentMetadata returnValue = underTest.getComponentMetadata();

        // Then
        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(1);
        ILoggingEvent event;
        event = events.get(0);
        assertThat(event.getFormattedMessage()).isEqualTo("Could not read Component Metadata file from repo \"https://example.com/repo-2.git\"");
        assertThat(event.getThrowableProxy().getClassName()).isEqualTo("com.fasterxml.jackson.databind.JsonMappingException");
        assertThat(event.getThrowableProxy().getMessage()).isEqualTo(""
                + "while parsing a flow node\n" + " in 'reader', line 3, column 1:\n"
                + "    \n"
                + "    ^\n"
                + "expected the node content, but found '<stream end>'\n"
                + " in 'reader', line 3, column 1:\n"
                + "    \n"
                + "    ^\n"
                + "\n"
                + " at [Source: (StringReader); line: 2, column: 14] (through reference chain: tech.kronicle.componentmetadata.models.ComponentMetadata$ComponentMetadataBuilder[\"components\"])");

        assertThat(returnValue.getAreas()).hasSize(2);
        assertThat(returnValue.getAreas().stream().map(Area::getId).collect(Collectors.toList())).containsExactly(
                "test-area-1",
                "test-area-2");
        assertThat(returnValue.getTeams()).hasSize(2);
        assertThat(returnValue.getTeams().stream().map(Team::getId).collect(Collectors.toList())).containsExactly(
                "test-team-1",
                "test-team-2");
        assertThat(returnValue.getComponents()).hasSize(2);
        assertThat(returnValue.getComponents().stream().map(Component::getId).collect(Collectors.toList())).containsExactly(
                "test-component-1",
                "test-component-2");
    }

    @Test
    public void getComponentMetadataShouldSkipARepoIfValidationFailsForItsComponentMetadata() throws IOException, GitAPIException, URISyntaxException {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("Repo1")).build());
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(RepoDirAndGit.builder().repoDir(getResourcesDir("RepoWithInvalidComponentMetadata")).build());
        mockRepoProviders(List.of(new ApiRepo(REPO_URL_1, true), new ApiRepo(REPO_URL_2, true)));
        underTest = new ComponentMetadataRepository(mockFinder, mockGitCloner, new YAMLMapper(), validatorService);

        // When
        ComponentMetadata returnValue = underTest.getComponentMetadata();

        // Then
        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(1);
        ILoggingEvent event;
        event = events.get(0);
        assertThat(event.getFormattedMessage()).isEqualTo("Could not read Component Metadata file from repo \"https://example.com/repo-2.git\"");
        assertThat(event.getThrowableProxy().getClassName()).isEqualTo("tech.kronicle.service.exceptions.ValidationException");
        assertThat(event.getThrowableProxy().getMessage()).isEqualTo(""
                + "Failed to validate tech.kronicle.componentmetadata.models.ComponentMetadata with reference \"component-metadata\". Violations:\n"
                + "- components[0].id with value \"Not-Valid-Id\" must match \"[a-z][a-z0-9]*(-[a-z0-9]+)*\"");

        assertThat(returnValue.getAreas()).hasSize(2);
        assertThat(returnValue.getAreas().stream().map(Area::getId).collect(Collectors.toList())).containsExactly(
                "test-area-1",
                "test-area-2");
        assertThat(returnValue.getTeams()).hasSize(2);
        assertThat(returnValue.getTeams().stream().map(Team::getId).collect(Collectors.toList())).containsExactly(
                "test-team-1",
                "test-team-2");
        assertThat(returnValue.getComponents()).hasSize(2);
        assertThat(returnValue.getComponents().stream().map(Component::getId).collect(Collectors.toList())).containsExactly(
                "test-component-1",
                "test-component-2");
    }

    private void mockRepoProviders(List<ApiRepo>... repos) {
        List<RepoProvider> repoProviders = Arrays.stream(repos)
                .map(FakeRepoProvider::new)
                .collect(Collectors.toList());
        when(mockFinder.getRepoProviders()).thenReturn(repoProviders);
    }

    @RequiredArgsConstructor
    private static class FakeRepoProvider extends RepoProvider {

        private final List<ApiRepo> apiRepos;

        @Override
        public List<ApiRepo> getApiRepos() {
            return apiRepos;
        }
    }
}
