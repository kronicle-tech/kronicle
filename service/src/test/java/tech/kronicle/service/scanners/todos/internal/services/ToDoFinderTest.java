package tech.kronicle.service.scanners.todos.internal.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ToDoFinderTest {

    private final ToDoFinder underTest = new ToDoFinder();

    @Test
    public void shouldFindDoubleSlashToDo() {
        // Given
        String input = "//TODOMust do this";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @Test
    public void shouldFindSlashStarToDo() {
        // Given
        String input = "/*TODOMust do this";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @Test
    public void shouldFindTerminatedSlashStarToDo() {
        // Given
        String input = "/*TODOMust do this*/";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @Test
    public void shouldFindTerminatedSlashStarToDoFollowedBySpace() {
        // Given
        String input = "/*TODOMust do this*/ ";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @Test
    public void shouldFindHashToDo() {
        // Given
        String input = "#TODOMust do this";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo", "TODO", "ToDo", "toDo"})
    public void shouldFindDifferentCapitalisationsOfToDo(String toDo) {
        // Given
        String input = "//" + toDo + " Must do this";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @ParameterizedTest
    @ValueSource(strings = {"TO DO", "TO  DO", "TO\tDO"})
    public void shouldFindToDoWithWhitespaceBetweenToAndDo(String toDo) {
        // Given
        String input = "//" + toDo + " Must do this";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @Test
    public void shouldFindDoubleSlashToDoWithSpacing() {
        // Given
        String input = "  //  TODO  Must do this  ";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @ParameterizedTest
    @ValueSource(strings = {":", "-"})
    public void shouldFindDoubleSlashToDoWithSeparator(String separator) {
        // Given
        String input = "  //  TODO  " + separator + "  Must do this  ";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @Test
    public void shouldFindDoubleSlashToDoSurroundedByNewLines() {
        // Given
        String input = "\n// TODO: Must do this\n";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @Test
    public void shouldNotIncludeMultipleLinesInToDo() {
        // Given
        String input = "\n// TODO: Must do this\nNot this";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this");
    }

    @Test
    public void shouldNotMatchToDoOutsideACodeComment() {
        // Given
        String input = "TODO: Must not match this";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).isEmpty();
    }

    @Test
    public void shouldMatchMultipleToDos() {
        // Given
        String input = "// TODO: Must do this\n// TODO: And this";

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(toDos).containsExactly("Must do this", "And this");
    }

    @Test
    public void shouldLimitToDoWithMoreThan97CharactersTo97Characters() {
        // Given
        String textWith98Characters = "a".repeat(98);
        String input = "// TODO: " + textWith98Characters;

        // When
        List<String> toDos = underTest.findToDos(input);

        // Then
        assertThat(textWith98Characters).hasSize(98);
        assertThat(toDos).containsExactly(textWith98Characters.substring(0, 97) + "...");
    }
}
