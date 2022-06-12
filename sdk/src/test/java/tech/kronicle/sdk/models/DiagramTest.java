package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.sdk.models.testutils.DiagramStateUtils.createDiagramState;

public class DiagramTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        Diagram returnValue = new ObjectMapper().readValue(json, Diagram.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableList() {
        // Given
        Diagram underTest = Diagram.builder().tags(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().add(Tag.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeConnectionsAnUnmodifiableList() {
        // Given
        Diagram underTest = Diagram.builder().connections(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getConnections().add(DiagramConnection.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeStatesAnUnmodifiableList() {
        // Given
        Diagram underTest = Diagram.builder().states(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getStates().add(createDiagramState(1)));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
    
    @Test
    public void addStateWhenThereAreNoExistingStatesShouldAddStateToExistingStates() {
        // Given
        DiagramState newState = createDiagramState(1);
        Diagram underTest = Diagram.builder().build();

        // When
        underTest = underTest.addState(newState);

        // When
        assertThat(underTest.getStates()).containsExactly(newState);
    }

    @Test
    public void addStateWhenThereAreExistingStatesShouldAddStateToExistingStates() {
        // Given
        DiagramState state2 = createDiagramState(2);
        DiagramState state3 = createDiagramState(3);
        List<DiagramState> existingState = List.of(
                state2,
                state3
        );
        DiagramState state1 = createDiagramState(1);
        Diagram underTest = Diagram.builder()
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
        List<DiagramState> newStates = List.of(
                createDiagramState(1),
                createDiagramState(2)
        );
        Diagram underTest = Diagram.builder().build();

        // When
        underTest = underTest.addStates(newStates);

        // When
        assertThat(underTest.getStates()).containsExactlyElementsOf(newStates);
    }

    @Test
    public void addStatesWhenThereAreExistingStatesShouldAddStatesToExistingStates() {
        // Given
        DiagramState state3 = createDiagramState(3);
        DiagramState state4 = createDiagramState(4);
        List<DiagramState> existingState = List.of(
                state3,
                state4
        );
        DiagramState state1 = createDiagramState(1);
        DiagramState state2 = createDiagramState(2);
        List<DiagramState> newState = List.of(
                state1,
                state2
        );
        Diagram underTest = Diagram.builder()
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
        Diagram underTest = Diagram.builder()
                .id("test-diagram-id")
                .build();

        // When
        List<DiagramState> returnValue = underTest.getStates("test-state-type");

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getStatesShouldReturnAnEmptyListWhenThereAreNoMatchingStates() {
        // Given
        Diagram underTest = Diagram.builder()
                .id("test-diagram-id")
                .states(List.of(
                        createDiagramState(1),
                        createDiagramState(2)
                ))
                .build();

        // When
        List<DiagramState> returnValue = underTest.getStates("test-state-type");

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getStatesShouldReturnStatesWithMatchingTypesWhenThereAreMatchingStates() {
        // Given
        DiagramState state1 = createDiagramState(1, "test-state-type-1");
        DiagramState state2 = createDiagramState(2, "test-state-type-1");
        DiagramState state3 = createDiagramState(3, "test-state-type-2");
        DiagramState state4 = createDiagramState(4, "test-state-type-2");
        DiagramState state5 = createDiagramState(5, "test-state-type-3");
        DiagramState state6 = createDiagramState(6, "test-state-type-3");
        Diagram underTest = Diagram.builder()
                .id("test-diagram-id")
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
        List<DiagramState> returnValue = underTest.getStates("test-state-type-2");

        // Then
        assertThat(returnValue).containsExactly(
                state3,
                state4
        );
    }
    
    @Test
    public void getStateShouldReturnNullWhenThereAreNoStates() {
        // Given
        Diagram underTest = Diagram.builder()
                .id("test-diagram-id")
                .build();

        // When
        DiagramState returnValue = underTest.getState("test-state-type");

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getStateShouldReturnNullWhenThereAreNoMatchingStates() {
        // Given
        Diagram underTest = Diagram.builder()
                .id("test-diagram-id")
                .states(List.of(
                        createDiagramState(1),
                        createDiagramState(2)
                ))
                .build();

        // When
        DiagramState returnValue = underTest.getState("test-state-type");

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getStateShouldReturnStateWithMatchingTypeWhenThereIsExactlyOneMatchingState() {
        // Given
        DiagramState state1 = createDiagramState(1, "test-state-type-1");
        DiagramState state2 = createDiagramState(2, "test-state-type-2");
        DiagramState state3 = createDiagramState(3, "test-state-type-3");
        Diagram underTest = Diagram.builder()
                .id("test-diagram-id")
                .states(List.of(
                        state1,
                        state2,
                        state3
                ))
                .build();

        // When
        DiagramState returnValue = underTest.getState("test-state-type-2");

        // Then
        assertThat(returnValue).isEqualTo(state2);
    }

    @Test
    public void getStateShouldThrowAnExceptionWhenThereIsMoreThanOneMatchingState() {
        // Given
        DiagramState state1 = createDiagramState(1, "test-state-type-1");
        DiagramState state2 = createDiagramState(2, "test-state-type-2");
        DiagramState state3 = createDiagramState(3, "test-state-type-2");
        DiagramState state4 = createDiagramState(4, "test-state-type-3");
        Diagram underTest = Diagram.builder()
                .id("test-diagram-id")
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
