package tech.kronicle.plugins.repostate;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.sdk.models.RepoReference;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoStateScannerTest extends BaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        RepoStateScanner underTest = new RepoStateScanner();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("repo-state");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        RepoStateScanner underTest = new RepoStateScanner();

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Copies state from repos to the components using those repos");
    }

    @Test
    public void notesShouldReturnNull() {
        // Given
        RepoStateScanner underTest = new RepoStateScanner();

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void scanShouldNotTransformComponentWhenComponentHasNoRepo() {
        // Given
        RepoStateScanner underTest = new RepoStateScanner();
        ComponentMetadata componentMetadata = createComponentMetadata();
        Component component = Component.builder()
                .id("test-component-id-1")
                .build();

        // When
        underTest.refresh(componentMetadata);
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue).isEqualTo(Output.empty(CACHE_TTL));
    }

    @Test
    public void scanShouldNotTransformComponentWhenRepoOfComponentIsNotInComponentMetadata() {
        // Given
        RepoStateScanner underTest = new RepoStateScanner();
        ComponentMetadata componentMetadata = createComponentMetadata();
        Component component = Component.builder()
                .id("test-component-id-1")
                .repo(createRepoReference("https://example.com/not-in-component-metadata"))
                .build();

        // When
        underTest.refresh(componentMetadata);
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue).isEqualTo(Output.empty(CACHE_TTL));
    }

    @Test
    public void scanShouldNotTransformComponentWhenRepoHasNoState() {
        // Given
        RepoStateScanner underTest = new RepoStateScanner();
        ComponentMetadata componentMetadata = createComponentMetadata();
        Component component = Component.builder()
                .id("test-component-id-1")
                .repo(createRepoReference(componentMetadata.getRepos().get(0).getUrl()))
                .build();

        // When
        underTest.refresh(componentMetadata);
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue).isEqualTo(Output.empty(CACHE_TTL));
    }

    @Test
    public void scanShouldMergeRepoStateAndComponentState() {
        // Given
        RepoStateScanner underTest = new RepoStateScanner();
        ComponentMetadata componentMetadata = createComponentMetadata(
                createComponentState(1)
        );
        Component component = Component.builder()
                .id("test-component-id-1")
                .repo(createRepoReference(componentMetadata.getRepos().get(0).getUrl()))
                .states(createComponentState(2))
                .build();

        // When
        underTest.refresh(componentMetadata);
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        Component transformedComponent = getMutatedComponent(returnValue, component);
        assertThat(transformedComponent).isEqualTo(
                component.withStates(
                        ComponentState.builder()
                                .environments(List.of(
                                        createEnvironmentState(2),
                                        createEnvironmentState(1)
                                ))
                                .build()
                )
        );
    }

    private ComponentState createComponentState(int componentStateNumber) {
        return ComponentState.builder()
                .environments(List.of(
                        createEnvironmentState(componentStateNumber)
                ))
                .build();
    }

    private EnvironmentState createEnvironmentState(int environmentNumber) {
        return EnvironmentState.builder()
                .id("test-environment-id-" + environmentNumber)
                .build();
    }

    private ComponentMetadata createComponentMetadata() {
        return createComponentMetadata(null);
    }

    private ComponentMetadata createComponentMetadata(ComponentState componentState) {
        return ComponentMetadata.builder()
                .repos(List.of(
                        createRepo(1, componentState),
                        createRepo(2)
                ))
                .build();
    }

    private Repo createRepo(int repoNumber) {
        return createRepo(repoNumber, null);
    }

    private Repo createRepo(int repoNumber, ComponentState componentState) {
        return Repo.builder()
                .url(createRepoUrl(repoNumber))
                .state(componentState)
                .build();
    }

    private String createRepoUrl(int repoNumber) {
        return "https://example.com/test-repo-" + repoNumber;
    }

    private RepoReference createRepoReference(String repoUrl) {
        return RepoReference.builder()
                .url(repoUrl)
                .build();
    }
}
