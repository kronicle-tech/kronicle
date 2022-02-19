package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.todos.ToDo;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

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
        Throwable thrown = catchThrowable(() -> underTest.getTags().add("test"));

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
}
