package com.moneysupermarket.componentcatalog.service.scanners.todos;

import com.moneysupermarket.componentcatalog.sdk.models.todos.ToDo;
import com.moneysupermarket.componentcatalog.service.scanners.CodebaseScanner;
import com.moneysupermarket.componentcatalog.service.scanners.models.Codebase;
import com.moneysupermarket.componentcatalog.service.scanners.models.Output;
import com.moneysupermarket.componentcatalog.service.scanners.todos.internal.services.ToDoFinder;
import com.moneysupermarket.componentcatalog.service.spring.stereotypes.Scanner;
import com.moneysupermarket.componentcatalog.service.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Scanner
@RequiredArgsConstructor
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
