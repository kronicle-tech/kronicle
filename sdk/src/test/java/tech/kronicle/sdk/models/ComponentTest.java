package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.todos.ToDo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

public class ComponentTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        Component returnValue = new ObjectMapper().readValue(json, Component.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldDeserializePluginsAsAMapOfRawJsonValues() throws JsonProcessingException {
        // Given
        String json = "{\n" +
                "  \"plugins\": {\n" +
                "    \"key-1\": {\n" +
                "      \"nested-key-1\": \"nested-value-1\"\n" +
                "    },\n" +
                "    \"key-2\": {\n" +
                "      \"nested-key-2\": \"nested-value-2\"\n" +
                "    }\n" +
                "  }" +
                "}";

        // When
        Component returnValue = new ObjectMapper().readValue(json, Component.class);

        // Then
        assertThat(returnValue).isNotNull();
        Map<String, String> plugins = returnValue.getPlugins();
        assertThat(plugins).isNotNull();
        assertThat(plugins.keySet()).containsExactlyInAnyOrder("key-1", "key-2");
        assertThat(plugins.get("key-1")).isEqualTo("{\"nested-key-1\":\"nested-value-1\"}");
        assertThat(plugins.get("key-2")).isEqualTo("{\"nested-key-2\":\"nested-value-2\"}");
    }

    @Test
    public void referenceShouldReturnId() {
        // Given
        Component underTest = Component.builder().id("test-id").build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-id");
    }

    @Test
    public void constructorShouldMakeAliasesAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().aliases(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getAliases().add(
                new Alias("test-alias-id", null, null)
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().tags(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().add(new Tag(null, null)));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeResponsibilitiesAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().responsibilities(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getResponsibilities().add(Responsibility.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTeamsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().teams(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTeams().add(ComponentTeam.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeCrossFunctionalRequirementsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().crossFunctionalRequirements(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getCrossFunctionalRequirements().add(CrossFunctionalRequirement.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTechDebtsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().techDebts(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTechDebts().add(TechDebt.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakePluginsAnUnmodifiableMap() {
        // Given
        Component underTest = Component.builder().plugins(new HashMap<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getPlugins().put("key", "value"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeDependenciesAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().dependencies(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDependencies().add(ComponentDependency.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeSoftwareRepositoriesAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().softwareRepositories(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSoftwareRepositories().add(SoftwareRepository.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeSoftwareAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().software(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSoftware().add(Software.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeImportsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().imports(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getImports().add(Import.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeKeySoftwareAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().keySoftware(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getKeySoftware().add(KeySoftware.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeToDosAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().toDos(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getToDos().add(ToDo.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeOpenApiSpecsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().openApiSpecs(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getOpenApiSpecs().add(OpenApiSpec.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeSonarQubeProjectsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().sonarQubeProjects(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSonarQubeProjects().add(SonarQubeProject.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeScannerErrorsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().scannerErrors(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getScannerErrors().add(ScannerError.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTestResultsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().testResults(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTestResults().add(TestResult.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void withUpdatedStateShouldPassANewStateObjectToActionWhenStateIsNull() {
        // Given
        ComponentState updatedState = createState(1);
        Component underTest = Component.builder().build();
        FakeStateUpdateAction action = new FakeStateUpdateAction(updatedState);

        // When
        Component returnValue = underTest.withUpdatedState(action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withState(updatedState));
        assertThat(action.calls).containsExactly(ComponentState.builder().build());
    }

    @Test
    public void withUpdatedStateShouldPassExistingStateObjectToActionWhenStateIsNotNull() {
        // Given
        ComponentState initialState = createState(1);
        ComponentState updatedState = createState(2);
        Component underTest = Component.builder()
                .state(initialState)
                .build();
        FakeStateUpdateAction action = new FakeStateUpdateAction(updatedState);

        // When
        Component returnValue = underTest.withUpdatedState(action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withState(updatedState));
        assertThat(action.calls).containsExactly(initialState);
    }

    private ComponentState createState(int stateNumber) {
        return ComponentState.builder()
                .environments(List.of(
                        EnvironmentState.builder()
                                .id("test-environment-id-" + stateNumber)
                                .build()
                ))
                .build();
    }

    @RequiredArgsConstructor
    private static class FakeStateUpdateAction {

        private final ComponentState updatedState;
        private final List<ComponentState> calls = new ArrayList<>();

        public ComponentState apply(ComponentState value) {
            calls.add(value);
            return updatedState;
        }
    }

    private final ObjectMapper objectMapper = new JsonMapper();

    @SneakyThrows
    @Test
    public void tagsShouldDeserializeFromJsonString() {
        // Given
        String rawJson = "{\"tags\":[\"test-tag-key\"]}";

        // When
        Component returnValue = objectMapper.readValue(rawJson, Component.class);

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .tags(List.of(
                                Tag.builder()
                                        .key("test-tag-key")
                                        .build()
                        ))
                        .build()
        );
        assertThat(returnValue.getTags().get(0)).isInstanceOf(Tag.class);
    }

    @SneakyThrows
    @Test
    public void tagsShouldDeserializeFromJsonObject() {
        // Given
        String rawJson = "{\"tags\":[{\"key\":\"test-tag-key\",\"value\":\"test-tag-value\"}]}";

        // When
        Component returnValue = objectMapper.readValue(rawJson, Component.class);

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .tags(List.of(
                                Tag.builder()
                                        .key("test-tag-key")
                                        .value("test-tag-value")
                                        .build()
                        ))
                        .build()
        );
        assertThat(returnValue.getTags().get(0)).isInstanceOf(Tag.class);
    }
}
