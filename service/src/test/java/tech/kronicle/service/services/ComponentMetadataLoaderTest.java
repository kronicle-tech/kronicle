package tech.kronicle.service.services;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.*;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.SimplifiedLogEvent;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.service.testutils.ValidatorServiceFactory.createValidationService;

public class ComponentMetadataLoaderTest {

    private LogCaptor logCaptor;
    private final ComponentMetadataLoader underTest = new ComponentMetadataLoader(createValidationService());

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(underTest.getClass());
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
    }

    @Test
    public void loadComponentMetadataShouldLoadAreas() {
        // Given
        Area area1 = createTestArea(1);
        Area area2 = createTestArea(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .areas(List.of(area1, area2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).containsOnly(
                Map.entry(area1.getId(), area1),
                Map.entry(area2.getId(), area2));
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldLoadTeams() {
        // Given
        Team team1 = createTestTeam(1);
        Team team2 = createTestTeam(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .teams(List.of(team1, team2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).containsOnly(
                Map.entry(team1.getId(), team1),
                Map.entry(team2.getId(), team2));
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldLoadComponents() {
        // Given
        Component component1 = createTestComponent(1);
        Component component2 = createTestComponent(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1), createTestComponentType(2)))
                .components(List.of(component1, component2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1),
                Map.entry(component2.getId(), component2));
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldLoadDiagrams() {
        // Given
        Diagram diagram1 = createTestDiagram(1);
        Diagram diagram2 = createTestDiagram(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .diagrams(List.of(diagram1, diagram2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).containsOnly(
                Map.entry(diagram1.getId(), diagram1),
                Map.entry(diagram2.getId(), diagram2));
    }

    @Test
    public void loadComponentMetadataShouldLoadAllTypes() {
        // Given
        Area area1 = createTestArea(1);
        Area area2 = createTestArea(2);
        Team team1 = createTestTeam(1);
        Team team2 = createTestTeam(2);
        Component component1 = createTestComponent(1);
        Component component2 = createTestComponent(2);
        Diagram diagram1 = createTestDiagram(1);
        Diagram diagram2 = createTestDiagram(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1), createTestComponentType(2)))
                .platforms(List.of(createTestPlatform(1), createTestPlatform(2)))
                .areas(List.of(area1, area2))
                .teams(List.of(team1, team2))
                .components(List.of(component1, component2))
                .diagrams(List.of(diagram1, diagram2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 diagrams")
        );
        assertThat(returnValue.getAreas()).containsOnly(
                Map.entry(area1.getId(), area1),
                Map.entry(area2.getId(), area2));
        assertThat(returnValue.getTeams()).containsOnly(
                Map.entry(team1.getId(), team1),
                Map.entry(team2.getId(), team2));
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1),
                Map.entry(component2.getId(), component2));
        assertThat(returnValue.getDiagrams()).containsOnly(
                Map.entry(diagram1.getId(), diagram1),
                Map.entry(diagram2.getId(), diagram2));
    }

    @Test
    public void loadComponentMetadataShouldSkipAnAreaWithADuplicateId() {
        // Given
        Area area1 = createTestArea(1, 1);
        Area area2 = createTestArea(1, 2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .areas(List.of(area1, area2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.ERROR, "Area id test-area-id-1 is defined at least twice and will be skipped this time"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).containsOnly(
                Map.entry(area1.getId(), area1));
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldSkipATeamWithADuplicateId() {
        // Given
        Team team1 = createTestTeam(1, 1);
        Team team2 = createTestTeam(1, 2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .teams(List.of(team1, team2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.ERROR, "Team id test-team-id-1 is defined at least twice and will be skipped this time"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).containsOnly(
                Map.entry(team1.getId(), team1));
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldSkipAComponentWithADuplicateId() {
        // Given
        Component component1 = createTestComponent(1, 1);
        Component component2 = createTestComponent(1, 2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1), createTestComponentType(2)))
                .components(List.of(component1, component2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.ERROR, "Component id test-component-id-1 is defined at least twice and will be skipped this time"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1));
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldSkipADiagramWithADuplicateId() {
        // Given
        Diagram diagram1 = createTestDiagram(1, 1);
        Diagram diagram2 = createTestDiagram(1, 2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .diagrams(List.of(diagram1, diagram2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.ERROR, "Diagram id test-diagram-id-1 is defined at least twice and will be skipped this time"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).containsOnly(
                Map.entry(diagram1.getId(), diagram1));
    }

    @Test
    public void loadComponentMetadataShouldSkipAnAreaThatFailsValidation() {
        // Given
        Area area1 = createInvalidTestArea(1);
        Area area2 = createTestArea(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .areas(List.of(area1, area2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.ERROR, "Area id test-area-id-1 failed validation and will be skipped"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(logCaptor.getEvents().get(2).getThrowableProxy().getMessage()).isEqualTo(""
            + "Failed to validate tech.kronicle.sdk.models.Area with reference \"test-area-id-1\". Violations:\n"
            + "- name with value \"null\" must not be blank");
        assertThat(returnValue.getAreas()).containsOnly(
                Map.entry(area2.getId(), area2));
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldSkipATeamThatFailsValidation() {
        // Given
        Team team1 = createInvalidTestTeam(1);
        Team team2 = createTestTeam(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .teams(List.of(team1, team2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.ERROR, "Team id test-team-id-1 failed validation and will be skipped"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(logCaptor.getEvents().get(3).getThrowableProxy().getMessage()).isEqualTo(""
                + "Failed to validate tech.kronicle.sdk.models.Team with reference \"test-team-id-1\". Violations:\n"
                + "- name with value \"null\" must not be blank");
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).containsOnly(
                Map.entry(team2.getId(), team2));
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldSkipAComponentThatFailsValidation() {
        // Given
        Component component1 = createInvalidTestComponent(1);
        Component component2 = createTestComponent(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1), createTestComponentType(2)))
                .components(List.of(component1, component2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 2 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.ERROR, "Component id test-component-id-1 failed validation and will be skipped"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(logCaptor.getEvents().get(4).getThrowableProxy().getMessage()).isEqualTo(""
                + "Failed to validate tech.kronicle.sdk.models.Component with reference \"test-component-id-1\". Violations:\n"
                + "- name with value \"null\" must not be blank\n"
                + "- typeId with value \"null\" must not be blank");
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component2.getId(), component2));
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldSkipADiagramThatFailsValidation() {
        // Given
        Diagram diagram1 = createInvalidTestDiagram(1);
        Diagram diagram2 = createTestDiagram(2);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .diagrams(List.of(diagram1, diagram2))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.ERROR, "Diagram id test-diagram-id-1 failed validation and will be skipped"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 diagrams")
        );
        assertThat(logCaptor.getEvents().get(5).getThrowableProxy().getMessage()).isEqualTo(""
                + "Failed to validate tech.kronicle.sdk.models.Diagram with reference \"test-diagram-id-1\". Violations:\n"
                + "- name with value \"null\" must not be blank");
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).containsOnly(
                Map.entry(diagram2.getId(), diagram2));
    }

    @Test
    public void loadComponentMetadataShouldLogAnErrorForATeamWithANonExistentArea() {
        // Given
        Team team1 = createTestTeamBuilder(1, 1)
                .areaId("test-area-id-1")
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .teams(List.of(team1))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.ERROR, "Cannot find area test-area-id-1 for team test-team-id-1"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).containsOnly(
                Map.entry(team1.getId(), team1));
        assertThat(returnValue.getComponents()).isEmpty();
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldLogAnErrorForAComponentWithANonExistentComponentType() {
        // Given
        Component component1 = createTestComponentBuilder(1, 1)
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(2)))
                .components(List.of(component1))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.ERROR, "Cannot find component type test-component-type-id-1 for component test-component-id-1"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1));
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldLogAnErrorForAComponentWithANonExistentTeam() {
        // Given
        Component component1 = createTestComponentBuilder(1, 1)
                .teams(List.of(ComponentTeam.builder().teamId("test-team-id-1").build()))
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1)))
                .components(List.of(component1))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.ERROR, "Cannot find team test-team-id-1 for component test-component-id-1"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1));
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldLogAnErrorForAComponentWithANonExistentPlatform() {
        // Given
        Component component1 = createTestComponentBuilder(1, 1)
                .platformId("test-platform-id-1")
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1)))
                .platforms(List.of(createTestPlatform(2)))
                .components(List.of(component1))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.ERROR, "Cannot find platform test-platform-id-1 for component test-component-id-1"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1));
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldLogAnErrorForAComponentWithANonExistentDependency() {
        // Given
        Component component1 = createTestComponentBuilder(1, 1)
                .dependencies(List.of(ComponentDependency.builder().targetComponentId("test-component-id-2").build()))
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1)))
                .components(List.of(component1))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.ERROR,
                        "Cannot find target component test-component-id-2 for dependency of component test-component-id-1"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 components"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1));
        assertThat(returnValue.getDiagrams()).isEmpty();
    }

    @Test
    public void loadComponentMetadataShouldLogAnErrorForADiagramWithANonExistentConnectionSourceComponent() {
        // Given
        Component component1 = createTestComponent(1);
        Diagram diagram1 = createTestDiagramBuilder(1, 1)
                .connections(List.of(DiagramConnection.builder()
                        .sourceComponentId("test-component-id-2")
                        .targetComponentId("test-component-id-1")
                        .build())
                )
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1)))
                .components(List.of(component1))
                .diagrams(List.of(diagram1))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 components"),
                new SimplifiedLogEvent(Level.ERROR, "Cannot find source component test-component-id-2 for connection of diagram test-diagram-id-1"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1));
        assertThat(returnValue.getDiagrams()).containsOnly(
                Map.entry(diagram1.getId(), diagram1));
    }

    @Test
    public void loadComponentMetadataShouldLogAnErrorForADiagramWithANonExistentConnectionTargetComponent() {
        // Given
        Component component1 = createTestComponent(1);
        Diagram diagram1 = createTestDiagramBuilder(1, 1)
                .connections(List.of(DiagramConnection.builder()
                        .sourceComponentId("test-component-id-1")
                        .targetComponentId("test-component-id-2")
                        .build())
                )
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .componentTypes(List.of(createTestComponentType(1)))
                .components(List.of(component1))
                .diagrams(List.of(diagram1))
                .build();

        // When
        ComponentMetadataLoader.Output returnValue = underTest.loadComponentMetadata(componentMetadata);

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 component types"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 platforms"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 areas"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 0 teams"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 components"),
                new SimplifiedLogEvent(Level.ERROR, "Cannot find target component test-component-id-2 for connection of diagram test-diagram-id-1"),
                new SimplifiedLogEvent(Level.INFO, "Loaded 1 diagrams")
        );
        assertThat(returnValue.getAreas()).isEmpty();
        assertThat(returnValue.getTeams()).isEmpty();
        assertThat(returnValue.getComponents()).containsOnly(
                Map.entry(component1.getId(), component1));
        assertThat(returnValue.getDiagrams()).containsOnly(
                Map.entry(diagram1.getId(), diagram1));
    }

    private ComponentType createTestComponentType(int idNumber) {
        return createTestComponentType(idNumber, idNumber);
    }

    private ComponentType createTestComponentType(int idNumber, int othersNumber) {
        return ComponentType.builder()
                .id("test-component-type-id-" + idNumber)
                .description("Test Component Type Description " + othersNumber)
                .build();
    }

    private Platform createTestPlatform(int idNumber) {
        return createTestPlatform(idNumber, idNumber);
    }

    private Platform createTestPlatform(int idNumber, int othersNumber) {
        return Platform.builder()
                .id("test-platform-id-" + idNumber)
                .description("Test Platform Description " + othersNumber)
                .build();
    }

    private Area createTestArea(int number) {
        return createTestArea(number, number);
    }

    private Area createTestArea(int idNumber, int othersNumber) {
        return Area.builder()
                .id("test-area-id-" + idNumber)
                .name("Test Area Name " + othersNumber)
                .build();
    }

    private Area createInvalidTestArea(int number) {
        return Area.builder()
                .id("test-area-id-" + number)
                .build();
    }

    private Team createTestTeam(int number) {
        return createTestTeam(number, number);
    }

    private Team createTestTeam(int idNumber, int othersNumber) {
        return createTestTeamBuilder(idNumber, othersNumber)
                .build();
    }

    private Team.TeamBuilder createTestTeamBuilder(int idNumber, int othersNumber) {
        return Team.builder()
                .id("test-team-id-" + idNumber)
                .name("Test Team Name " + othersNumber)
                .emailAddress("test-team-" + othersNumber + "@example.com");
    }

    private Team createInvalidTestTeam(int number) {
        return Team.builder()
                .id("test-team-id-" + number)
                .build();
    }

    private Component createTestComponent(int number) {
        return createTestComponent(number, number);
    }

    private Component createTestComponent(int idNumber, int othersNumber) {
        return createTestComponentBuilder(idNumber, othersNumber)
                .build();
    }

    private Component.ComponentBuilder createTestComponentBuilder(int idNumber, int othersNumber) {
        return Component
                .builder()
                .id("test-component-id-" + idNumber)
                .name("Test Component Name " + othersNumber)
                .repo(RepoReference.builder().url("http://example.com/test-repo-" + othersNumber + ".git").build())
                .typeId("test-component-type-id-" + othersNumber);
    }

    private Component createInvalidTestComponent(int number) {
        return Component.builder()
                .id("test-component-id-" + number)
                .build();
    }

    private Diagram createTestDiagram(int number) {
        return createTestDiagram(number, number);
    }

    private Diagram createTestDiagram(int idNumber, int othersNumber) {
        return createTestDiagramBuilder(idNumber, othersNumber)
                .build();
    }

    private Diagram.DiagramBuilder createTestDiagramBuilder(int idNumber, int othersNumber) {
        return Diagram
                .builder()
                .id("test-diagram-id-" + idNumber)
                .name("Test Diagram Name " + othersNumber);
    }

    private Diagram createInvalidTestDiagram(int number) {
        return Diagram.builder()
                .id("test-diagram-id-" + number)
                .build();
    }
}
