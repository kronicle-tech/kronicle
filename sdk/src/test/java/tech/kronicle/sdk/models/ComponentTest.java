package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.sdk.models.testutils.ComponentStateUtils.createComponentState;
import static tech.kronicle.sdk.models.testutils.TagUtils.createTag;

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
        Throwable thrown = catchThrowable(() -> underTest.getTags().add(Tag.builder().build()));

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
    public void constructorShouldMakeDocsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().docs(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDocs().add(
                Doc.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeOpenApiSpecsAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().openApiSpecs(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getOpenApiSpecs().add(
                OpenApiSpec.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeGraphQlSchemasAnUnmodifiableList() {
        // Given
        Component underTest = Component.builder().graphQlSchemas(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getGraphQlSchemas().add(
                GraphQlSchema.builder().build()
        ));

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
    public void addTagWhenThereAreNoExistingTagsShouldAddTagToExistingTags() {
        // Given
        Tag newTag = createTag(1);
        Component underTest = Component.builder().build();

        // When
        underTest = underTest.addTag(newTag);

        // When
        assertThat(underTest.getTags()).containsExactly(newTag);
    }

    @Test
    public void addTagWhenThereAreExistingTagsShouldAddTagToExistingTags() {
        // Given
        Tag tag2 = createTag(2);
        Tag tag3 = createTag(3);
        List<Tag> existingTag = List.of(
                tag2,
                tag3
        );
        Tag tag1 = createTag(1);
        Component underTest = Component.builder()
                .tags(existingTag)
                .build();

        // When
        underTest = underTest.addTag(tag1);

        // When
        assertThat(underTest.getTags()).containsExactly(
                tag2,
                tag3,
                tag1
        );
    }

    @Test
    public void addTagsWhenThereAreNoExistingTagsShouldAddTagsToExistingTags() {
        // Given
        List<Tag> newTags = List.of(
                createTag(1),
                createTag(2)
        );
        Component underTest = Component.builder().build();

        // When
        underTest = underTest.addTags(newTags);

        // When
        assertThat(underTest.getTags()).containsExactlyElementsOf(newTags);
    }

    @Test
    public void addTagsWhenThereAreExistingTagsShouldAddTagsToExistingTags() {
        // Given
        Tag tag3 = createTag(3);
        Tag tag4 = createTag(4);
        List<Tag> existingTag = List.of(
                tag3,
                tag4
        );
        Tag tag1 = createTag(1);
        Tag tag2 = createTag(2);
        List<Tag> newTag = List.of(
                tag1,
                tag2
        );
        Component underTest = Component.builder()
                .tags(existingTag)
                .build();

        // When
        underTest = underTest.addTags(newTag);

        // When
        assertThat(underTest.getTags()).containsExactly(
                tag3,
                tag4,
                tag1,
                tag2
        );
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
    public void getStatesShouldReturnAnEmptyListWhenThereAreNoStates() {
        // Given
        Component underTest = Component.builder()
                .id("test-component-id")
                .build();

        // When
        List<ComponentState> returnValue = underTest.getStates("test-state-type");

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getStatesShouldReturnAnEmptyListWhenThereAreNoMatchingStates() {
        // Given
        Component underTest = Component.builder()
                .id("test-component-id")
                .states(List.of(
                        createComponentState(1),
                        createComponentState(2)
                ))
                .build();

        // When
        List<ComponentState> returnValue = underTest.getStates("test-state-type");

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getStatesShouldStatesWithMatchingTypesWhenThereAreMatchingStates() {
        // Given
        ComponentState state1 = createComponentState(1, "test-state-type-1");
        ComponentState state2 = createComponentState(2, "test-state-type-1");
        ComponentState state3 = createComponentState(3, "test-state-type-2");
        ComponentState state4 = createComponentState(4, "test-state-type-2");
        ComponentState state5 = createComponentState(5, "test-state-type-3");
        ComponentState state6 = createComponentState(6, "test-state-type-3");
        Component underTest = Component.builder()
                .id("test-component-id")
                .states(List.of(
                        state1,
                        state2,
                        state3,
                        state4,
                        state5,
                        state6
                ))
                .build();

        // When
        List<ComponentState> returnValue = underTest.getStates("test-state-type-2");

        // Then
        assertThat(returnValue).containsExactly(
                state3,
                state4
        );
    }

    @Test
    public void getStateShouldReturnNullWhenThereAreNoStates() {
        // Given
        Component underTest = Component.builder()
                .id("test-component-id")
                .build();

        // When
        ComponentState returnValue = underTest.getState("test-state-type");

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getStateShouldReturnNullWhenThereAreNoMatchingStates() {
        // Given
        Component underTest = Component.builder()
                .id("test-component-id")
                .states(List.of(
                        createComponentState(1),
                        createComponentState(2)
                ))
                .build();

        // When
        ComponentState returnValue = underTest.getState("test-state-type");

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getStateShouldReturnStateWithMatchingTypeWhenThereIsExactlyOneMatchingState() {
        // Given
        ComponentState state1 = createComponentState(1, "test-state-type-1");
        ComponentState state2 = createComponentState(2, "test-state-type-2");
        ComponentState state3 = createComponentState(3, "test-state-type-3");
        Component underTest = Component.builder()
                .id("test-component-id")
                .states(List.of(
                        state1,
                        state2,
                        state3
                ))
                .build();

        // When
        ComponentState returnValue = underTest.getState("test-state-type-2");

        // Then
        assertThat(returnValue).isEqualTo(state2);
    }

    @Test
    public void getStateShouldThrowAnExceptionWhenThereIsMoreThanOneMatchingState() {
        // Given
        ComponentState state1 = createComponentState(1, "test-state-type-1");
        ComponentState state2 = createComponentState(2, "test-state-type-2");
        ComponentState state3 = createComponentState(3, "test-state-type-2");
        ComponentState state4 = createComponentState(4, "test-state-type-3");
        Component underTest = Component.builder()
                .id("test-component-id")
                .states(List.of(
                        state1,
                        state2,
                        state3,
                        state4
                ))
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getState("test-state-type-2"));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage("There are more than 1 states with type \"test-state-type-2\"");
    }
}
