package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.todos.ToDo;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.sdk.models.testutils.ComponentStateUtils.createComponentState;
import static tech.kronicle.sdk.models.testutils.ImportUtils.createImport;
import static tech.kronicle.sdk.models.testutils.SoftwareRepositoryUtils.createSoftwareRepository;
import static tech.kronicle.sdk.models.testutils.SoftwareUtils.createSoftware;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

public class ComponentTest {

    private final ObjectMapper objectMapper = new JsonMapper();

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
    public void constructorShouldMakeStatesAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().states(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getStates().add(createComponentState(1)));

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
    public void constructorShouldMakeGraphQlSchemasAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().graphQlSchemas(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getGraphQlSchemas().add(GraphQlSchema.builder().build()));

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

    @Test
    public void addStateWhenThereAreNoExistingStatesShouldAddStateToExistingStates() {
        // Given
        ComponentState newState = createComponentState(1);
        Component underTest = Component.builder().build();

        // When
        underTest = underTest.addState(newState);

        // When
        assertThat(underTest.getStates()).containsExactly(newState);
    }

    @Test
    public void addStateWhenThereAreExistingStatesShouldAddStateToExistingStates() {
        // Given
        ComponentState state2 = createComponentState(2);
        ComponentState state3 = createComponentState(3);
        List<ComponentState> existingState = List.of(
                state2,
                state3
        );
        ComponentState state1 = createComponentState(1);
        Component underTest = Component.builder()
                .states(existingState)
                .build();

        // When
        underTest = underTest.addState(state1);

        // When
        assertThat(underTest.getStates()).containsExactly(
                state2,
                state3,
                state1
        );
    }

    @Test
    public void addStatesWhenThereAreNoExistingStatesShouldAddStatesToExistingStates() {
        // Given
        List<ComponentState> newStates = List.of(
                createComponentState(1),
                createComponentState(2)
        );
        Component underTest = Component.builder().build();

        // When
        underTest = underTest.addStates(newStates);

        // When
        assertThat(underTest.getStates()).containsExactlyElementsOf(newStates);
    }

    @Test
    public void addStatesWhenThereAreExistingStatesShouldAddStatesToExistingStates() {
        // Given
        ComponentState state3 = createComponentState(3);
        ComponentState state4 = createComponentState(4);
        List<ComponentState> existingState = List.of(
                state3,
                state4
        );
        ComponentState state1 = createComponentState(1);
        ComponentState state2 = createComponentState(2);
        List<ComponentState> newState = List.of(
                state1,
                state2
        );
        Component underTest = Component.builder()
                .states(existingState)
                .build();

        // When
        underTest = underTest.addStates(newState);

        // When
        assertThat(underTest.getStates()).containsExactly(
                state3,
                state4,
                state1,
                state2
        );
    }

    @Test
    public void addImportsWhenThereAreNoExistingImportsShouldAddImportsToExistingImports() {
        // Given
        List<Import> newImports = List.of(
                createImport(1),
                createImport(2)
        );
        Component underTest = Component.builder().build();

        // When
        underTest = underTest.addImports(newImports);

        // When
        assertThat(underTest.getImports()).containsExactlyElementsOf(newImports);
    }

    @Test
    public void addImportsWhenThereAreExistingImportsShouldAddImportsToExistingImports() {
        // Given
        List<Import> existingImports = List.of(
                createImport(3),
                createImport(4)
        );
        List<Import> newImports = List.of(
                createImport(1),
                createImport(2)
        );
        Component underTest = Component.builder()
                .imports(existingImports)
                .build();

        // When
        underTest = underTest.addImports(newImports);

        // When
        assertThat(underTest.getImports()).containsExactlyElementsOf(
                unmodifiableUnionOfLists(List.of(existingImports, newImports))
        );
    }

    @Test
    public void addSoftwareRepositoriesWhenThereAreNoExistingSoftwareRepositoriesShouldAddSoftwareRepositoriesToExistingSoftwareRepositories() {
        // Given
        List<SoftwareRepository> newSoftwareRepositories = List.of(
                createSoftwareRepository(1),
                createSoftwareRepository(2)
        );
        Component underTest = Component.builder().build();

        // When
        underTest = underTest.addSoftwareRepositories(newSoftwareRepositories);

        // When
        assertThat(underTest.getSoftwareRepositories()).containsExactlyElementsOf(newSoftwareRepositories);
    }

    @Test
    public void addSoftwareRepositoriesWhenThereAreExistingSoftwareRepositoriesShouldAddSoftwareRepositoriesToExistingSoftwareRepositories() {
        // Given
        List<SoftwareRepository> existingSoftwareRepositories = List.of(
                createSoftwareRepository(3),
                createSoftwareRepository(4)
        );
        List<SoftwareRepository> newSoftwareRepositories = List.of(
                createSoftwareRepository(1),
                createSoftwareRepository(2)
        );
        Component underTest = Component.builder()
                .softwareRepositories(existingSoftwareRepositories)
                .build();

        // When
        underTest = underTest.addSoftwareRepositories(newSoftwareRepositories);

        // When
        assertThat(underTest.getSoftwareRepositories()).containsExactlyElementsOf(
                unmodifiableUnionOfLists(List.of(existingSoftwareRepositories, newSoftwareRepositories))
        );
    }

    @Test
    public void addSoftwareWhenThereAreNoExistingSoftwareShouldAddSoftwareToExistingSoftware() {
        // Given
        List<Software> newSoftware = List.of(
                createSoftware(1),
                createSoftware(2)
        );
        Component underTest = Component.builder().build();

        // When
        underTest = underTest.addSoftware(newSoftware);

        // When
        assertThat(underTest.getSoftware()).containsExactlyElementsOf(newSoftware);
    }

    @Test
    public void addSoftwareWhenThereAreExistingSoftwareShouldAddSoftwareToExistingSoftware() {
        // Given
        List<Software> existingSoftware = List.of(
                createSoftware(3),
                createSoftware(4)
        );
        List<Software> newSoftware = List.of(
                createSoftware(1),
                createSoftware(2)
        );
        Component underTest = Component.builder()
                .software(existingSoftware)
                .build();

        // When
        underTest = underTest.addSoftware(newSoftware);

        // When
        assertThat(underTest.getSoftware()).containsExactlyElementsOf(
                unmodifiableUnionOfLists(List.of(existingSoftware, newSoftware))
        );
    }
}
