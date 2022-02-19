package tech.kronicle.plugins.todo.internal.services;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ToDoFinder {

    private static final Pattern toDoPattern = Pattern.compile("(?i)(?://|/\\*|#)\\s*TO\\s*DO\\s*[-:]?(.+)");
    public static final int MAX_TODO_LENGTH = 97;

    public List<String> findToDos(String contents) {
        return toDoPattern.matcher(contents).results()
                .map(matchResult -> matchResult.group(1).strip().replaceAll("\\*/\\s*$", ""))
                .filter(toDo -> !toDo.isEmpty())
                .map(toDo -> toDo.length() <= MAX_TODO_LENGTH ? toDo : toDo.substring(0, MAX_TODO_LENGTH) + "...")
                .collect(Collectors.toList());
    }
}
