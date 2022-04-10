package tech.kronicle.service.repositories;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.pluginapi.git.GitCloner;
import tech.kronicle.testutils.BaseTest;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.sdk.models.Area;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Team;
import tech.kronicle.service.services.RepoFinderService;
import tech.kronicle.service.services.ValidatorService;
import tech.kronicle.service.testutils.ValidatorServiceFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;

@ExtendWith(MockitoExtension.class)
public class ComponentMetadataRepositoryTest extends BaseTest {

    private static final String REPO_URL_1 = "https://example.com/repo-1.git";
    private static final String REPO_URL_2 = "https://example.com/repo-2.git";
    private static final String REPO_URL_3 = "https://example.com/repo-3.git";
    private static final String REPO_URL_4 = "https://example.com/repo-4.git";

    @Mock
    private RepoFinderService mockRepoFinderService;
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
    public void getComponentMetadataShouldGetComponentMetadataFromMultipleRepos() {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(getResourcesDir("Repo1"));
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(getResourcesDir("Repo2"));
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_3)).thenReturn(getResourcesDir("Repo3"));
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_4)).thenReturn(getResourcesDir("Repo4"));
        mockRepoFinderService(
                new Repo(REPO_URL_1, true),
                new Repo(REPO_URL_2, true),
                new Repo(REPO_URL_3, true),
                new Repo(REPO_URL_4, true));
        underTest = createUnderTest();
        
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
    public void getComponentMetadataShouldIgnoreARepoWithNoComponentMetadataFile() {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(getResourcesDir("Repo2"));
        mockRepoFinderService(new Repo(REPO_URL_1, false), new Repo(REPO_URL_2, true));
        underTest = createUnderTest();

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
    public void getComponentMetadataShouldSkipARepoIfCloningOrPullingTheRepoFails() {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(getResourcesDir("Repo1"));
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenThrow(new RuntimeException("Test Exception"));
        mockRepoFinderService(new Repo(REPO_URL_1, true), new Repo(REPO_URL_2, true));
        underTest = createUnderTest();

        // When
        ComponentMetadata returnValue = underTest.getComponentMetadata();

        // Then
        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(1);
        ILoggingEvent event;
        event = events.get(0);
        assertThat(event.getFormattedMessage()).isEqualTo("Could not read Component Metadata file from repo \"https://example.com/repo-2.git\"");
        assertThat(event.getThrowableProxy().getClassName()).isEqualTo("java.lang.RuntimeException");
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
    public void getComponentMetadataShouldSkipARepoIfItSaysItContainsAComponentMetadataButActuallyDoesNotContainOne() {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(getResourcesDir("Repo1"));
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(getResourcesDir("RepoWithNoComponentMetadata"));
        mockRepoFinderService(new Repo(REPO_URL_1, true), new Repo(REPO_URL_2, true));
        underTest = createUnderTest();

        // When
        ComponentMetadata returnValue = underTest.getComponentMetadata();

        // Then
        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(1);
        ILoggingEvent event;
        event = events.get(0);
        assertThat(event.getFormattedMessage()).isEqualTo("Could not read Component Metadata file from repo \"https://example.com/repo-2.git\"");
        assertThat(event.getThrowableProxy().getClassName()).isEqualTo("java.lang.RuntimeException");
        assertThat(event.getThrowableProxy().getMessage()).isEqualTo(
                "Could not find Kronicle metadata file in repo \"https://example.com/repo-2.git\"");

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
    public void getComponentMetadataShouldSkipARepoIfItContainsInvalidYaml() {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(getResourcesDir("Repo1"));
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(getResourcesDir("RepoWithInvalidYaml"));
        mockRepoFinderService(new Repo(REPO_URL_1, true), new Repo(REPO_URL_2, true));
        underTest = createUnderTest();

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
                + " at [Source: (StringReader); line: 2, column: 14] (through reference chain: tech.kronicle.sdk.models.ComponentMetadata$ComponentMetadataBuilder[\"components\"])");

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
    public void getComponentMetadataShouldSkipARepoIfValidationFailsForItsComponentMetadata() {
        // Given
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_1)).thenReturn(getResourcesDir("Repo1"));
        when(mockGitCloner.cloneOrPullRepo(REPO_URL_2)).thenReturn(getResourcesDir("RepoWithInvalidComponentMetadata"));
        mockRepoFinderService(new Repo(REPO_URL_1, true), new Repo(REPO_URL_2, true));
        underTest = createUnderTest();

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
                + "Failed to validate tech.kronicle.sdk.models.ComponentMetadata with reference \"component-metadata\". Violations:\n"
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

    private ComponentMetadataRepository createUnderTest() {
        return new ComponentMetadataRepository(mockRepoFinderService, mockGitCloner, createFileUtils(), new YAMLMapper(), validatorService);
    }

    private void mockRepoFinderService(Repo... repos) {
        when(mockRepoFinderService.findRepos()).thenReturn(List.of(repos));
    }
}
