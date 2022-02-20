package tech.kronicle.service.utils;

import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringComponent
public class AntStyleIgnoreFileLoader {

    // "\\R" matches any Unicode line-break sequence on Java 8+
    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\\R");
    private static final Pattern COMMENT_LINE_PATTERN = Pattern.compile("^\\s*#");
    private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("^\\s*$");
    public static final Predicate<String> NOT_COMMENT_LINE = Predicate.not(COMMENT_LINE_PATTERN.asPredicate());
    public static final Predicate<String> NOT_EMPTY_LINE = Predicate.not(EMPTY_LINE_PATTERN.asPredicate());

    public List<String> load(String content) {
        return LINE_BREAK_PATTERN.splitAsStream(content)
                .filter(NOT_COMMENT_LINE)
                .filter(NOT_EMPTY_LINE)
                .collect(Collectors.toUnmodifiableList());
    }
}
