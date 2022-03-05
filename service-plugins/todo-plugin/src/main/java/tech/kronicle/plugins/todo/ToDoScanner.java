package tech.kronicle.plugins.todo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.todo.internal.services.ToDoFinder;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.sdk.models.todos.ToDo;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class ToDoScanner extends CodebaseScanner {

    private final FileUtils fileUtils;
    private final ToDoFinder toDoFinder;

    @Override
    public String id() {
        return "to-do";
    }

    @Override
    public String description() {
        return "Scans a component's codebase looking for any TODO comments that look like `// TODO:` or `# TODO:`";
    }

    @Override
    public Output<Void> scan(Codebase input) {
        List<ToDo> toDos = fileUtils.findFileContents(input.getDir())
                .flatMap(fileContent -> toDoFinder.findToDos(fileContent.getContent()).stream()
                        .map(toDo -> new ToDo(getRelativeFilePath(input, fileContent.getFile()), toDo)))
                .collect(Collectors.toList());
        log.info("Found {} To Dos in codebase \"{}\"", toDos.size(), input.getDir());
        return Output.of(component -> component.withToDos(toDos));
    }

    private String getRelativeFilePath(Codebase input, Path file) {
        return input.getDir().relativize(file).toString();
    }
}
