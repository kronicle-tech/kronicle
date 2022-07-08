package tech.kronicle.sdk.constants;

public final class PatternStrings {

    public static final String ID = "[a-z][a-z0-9]*([.-][a-z0-9]+)*";
    public static final String CASE_INSENSITIVE_SNAKE_CASE_OR_KEBAB_CASE = "[a-zA-Z][a-zA-Z0-9]*(_[a-zA-Z0-9]+)*|[a-zA-Z][a-zA-Z0-9]*(-[a-zA-Z0-9]+)*";

    private PatternStrings() {
    }
}
