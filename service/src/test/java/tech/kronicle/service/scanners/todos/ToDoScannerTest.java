package tech.kronicle.service.scanners.todos;

import tech.kronicle.sdk.models.todos.ToDo;
import tech.kronicle.service.scanners.BaseCodebaseScannerTest;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;
import tech.kronicle.service.scanners.todos.internal.services.ToDoFinder;
import tech.kronicle.service.testutils.MalformedFileCreator;
import tech.kronicle.service.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.service.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ToDoScannerTest extends BaseCodebaseScannerTest {

    private final ToDoScanner underTest = new ToDoScanner(new FileUtils(new AntStyleIgnoreFileLoader()), new ToDoFinder());

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("to-do");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a component's codebase looking for any TODO comments that look like `// TODO:` or `# TODO:`");
    }

    @Test
    public void notesShouldReturnNull() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void scanShouldFindMultipleToDosInMultipleFiles() {
        // Given
        Codebase testCodebase = new Codebase(getTestRepo(), getCodebaseDir("MultipleToDosInMultipleFiles"));

        // When
        Output<Void> returnValue = underTest.scan(testCodebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<ToDo> toDos = getToDos(returnValue);
        assertThat(toDos).hasSize(3);
        ToDo toDo;
        toDo = toDos.get(0);
        assertThat(toDo.getFile()).isEqualTo("example.sh");
        assertThat(toDo.getDescription()).isEqualTo("Must add something");
        toDo = toDos.get(1);
        assertThat(toDo.getFile()).isEqualTo("src/main/java/com/example/HelloWorld.java");
        assertThat(toDo.getDescription()).isEqualTo("Return greeting");
        toDo = toDos.get(2);
        assertThat(toDo.getFile()).isEqualTo("src/main/java/com/example/HelloWorld.java");
        assertThat(toDo.getDescription()).isEqualTo("Add more methods");
        assertThat(returnValue.getErrors()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void scanShouldSkipAMalformedFile(boolean malformed) {
        // Given
        Codebase testCodebase = new Codebase(getTestRepo(), getCodebaseDir("MalformedFile"));
        Path file = getCodebaseDir("MalformedFile").resolve("malformed_file.txt");
        String toDoText = "\n// TODO: In malformed file";
        MalformedFileCreator.createFile(file, malformed, null, toDoText);

        // When
        Output<Void> returnValue = underTest.scan(testCodebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<ToDo> toDos = getToDos(returnValue);
        assertThat(toDos).hasSize(malformed ? 1 : 2);
        ToDo toDo;
        int toDoIndex = 0;
        if (!malformed) {
            toDo = toDos.get(toDoIndex);
            assertThat(toDo.getFile()).isEqualTo("malformed_file.txt");
            assertThat(toDo.getDescription()).isEqualTo("In malformed file");
            assertThat(returnValue.getErrors()).isEmpty();
            toDoIndex++;
        }
        toDo = toDos.get(toDoIndex);
        assertThat(toDo.getFile()).isEqualTo("regular_file.txt");
        assertThat(toDo.getDescription()).isEqualTo("This should be found");
        assertThat(returnValue.getErrors()).isEmpty();
    }

    private List<ToDo> getToDos(Output<Void> returnValue) {
        return getMutatedComponent(returnValue).getToDos().stream()
          .sorted(Comparator.comparing(ToDo::getFile))
          .collect(Collectors.toList());
    }
}
