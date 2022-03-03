package tech.kronicle.common;

import static java.util.Objects.isNull;

public final class CaseUtils {

    private static final char SPACE_CHARACTER = ' ';
    private static final char HYPHEN_CHARACTER = '-';
    private static final char UNDERSCORE_CHARACTER = '_';
    private static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";
    public static final String HYPHEN_STRING = "-";
    public static final String UNDERSCORE_STRING = "_";

    public static String toKebabCase(String value) {
        return toKebabOrSnakeOrScreamingSnakeCase(value, LetterBehaviour.LOWER_CASE, HYPHEN_CHARACTER);
    }

    public static String toSnakeCase(String value) {
        return toKebabOrSnakeOrScreamingSnakeCase(value, LetterBehaviour.LOWER_CASE, UNDERSCORE_CHARACTER);
    }

    public static String toScreamingSnakeCase(String value) {
        return toKebabOrSnakeOrScreamingSnakeCase(value, LetterBehaviour.UPPER_CASE, UNDERSCORE_CHARACTER);
    }

    public static String toCamelCase(String value) {
        return toCamelOrPascalOrTitleCase(value, LetterBehaviour.LOWER_CASE, false);
    }

    public static String toPascalCase(String value) {
        return toCamelOrPascalOrTitleCase(value, LetterBehaviour.UPPER_CASE, false);
    }

    public static String toTitleCase(String value) {
        return toCamelOrPascalOrTitleCase(value, LetterBehaviour.UPPER_CASE, true);
    }

    private static String toCamelOrPascalOrTitleCase(String value, LetterBehaviour firstLetterBehaviour, boolean insertSpace) {
        if (isNull(value)) {
            return null;
        }

        if (value.isEmpty()) {
            return value;
        }

        StringBuilder stringBuilder = new StringBuilder(value.length() * 2);
        boolean firstLetterOrDigit = true;
        CharacterType lastCharacterType = null;

        for (int index = 0, count = value.length(); index < count; index++) {
            char character = value.charAt(index);
            CharacterType characterType = getCharacterType(character);

            if (characterType != CharacterType.OTHER) {
                if (firstLetterOrDigit) {
                    stringBuilder.append(toDesiredCase(character, firstLetterBehaviour));
                    firstLetterOrDigit = false;
                } else {
                    if (lastCharacterType == CharacterType.OTHER) {
                        if (insertSpace) {
                            stringBuilder.append(SPACE_CHARACTER);
                        }
                        stringBuilder.append(Character.toUpperCase(character));
                    } else {
                        if (lastCharacterType != characterType) {
                            if (insertSpace && (characterType == CharacterType.UPPER_CASE_LETTER)) {
                                stringBuilder.append(SPACE_CHARACTER);
                            }
                            stringBuilder.append(character);
                        } else {
                            stringBuilder.append(Character.toLowerCase(character));
                        }
                    }
                }
            }

            lastCharacterType = characterType;
        }

        return stringBuilder.toString();
    }

    private static String toKebabOrSnakeOrScreamingSnakeCase(String value, LetterBehaviour letterBehaviour, char delimiter) {
        if (isNull(value)) {
            return null;
        }

        if (value.isEmpty()) {
            return value;
        }

        StringBuilder stringBuilder = new StringBuilder(value.length() * 2);
        boolean insertDelimiterBeforeCapitalLetter = doesNotContainAnyDelimiters(value) && notAllSameCase(value);

        for (int index = 0, count = value.length(); index < count; index++) {
            char character = value.charAt(index);
            CharacterType characterType = getCharacterType(character);

            switch (characterType) {
                case DIGIT:
                    stringBuilder.append(character);
                    break;
                case LOWER_CASE_LETTER:
                    stringBuilder.append(toDesiredCase(character, letterBehaviour));
                    break;
                case UPPER_CASE_LETTER:
                    if (insertDelimiterBeforeCapitalLetter) {
                        stringBuilder.append(delimiter);
                    }

                    stringBuilder.append(toDesiredCase(character, letterBehaviour));
                    break;
                default:
                    stringBuilder.append(delimiter);
                    break;
            }
        }

        String delimiterText = Character.toString(delimiter);
        return stringBuilder.toString()
                .replaceAll("^" + delimiterText + "+", EMPTY_STRING)
                .replaceAll(delimiterText + "+$", EMPTY_STRING)
                .replaceAll(delimiterText + "{2,}", delimiterText);
    }

    private static boolean doesNotContainAnyDelimiters(String value) {
        return !value.contains(UNDERSCORE_STRING) && !value.contains(HYPHEN_STRING) && !value.contains(SPACE_STRING);
    }

    private static boolean notAllSameCase(String value) {
        return !value.toLowerCase().equals(value) && !value.toUpperCase().equals(value);
    }

    private static CharacterType getCharacterType(char character) {
        if (character >= 'a' && character <= 'z') {
            return CharacterType.LOWER_CASE_LETTER;
        } else if (character >= 'A' && character <= 'Z') {
            return CharacterType.UPPER_CASE_LETTER;
        } else if (character >= '0' && character <= '9') {
            return CharacterType.DIGIT;
        } else {
            return CharacterType.OTHER;
        }
    }

    private static char toDesiredCase(char character, LetterBehaviour letterBehaviour) {
        if (letterBehaviour == LetterBehaviour.LOWER_CASE) {
            return Character.toLowerCase(character);
        } else {
            return Character.toUpperCase(character);
        }
    }

    private enum LetterBehaviour {

        UPPER_CASE,
        LOWER_CASE
    }

    private enum CharacterType {

        DIGIT,
        LOWER_CASE_LETTER,
        OTHER,
        UPPER_CASE_LETTER
    }
}
