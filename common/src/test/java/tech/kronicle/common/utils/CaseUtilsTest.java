package tech.kronicle.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CaseUtilsTest {
    
    @Test
    public void toKebabCaseShouldHandleNull() {
        // Given
        String input = null;

        // When
        String returnValue = CaseUtils.toKebabCase(input);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void toSnakeCaseShouldHandleNull() {
        // Given
        String input = null;

        // When
        String returnValue = CaseUtils.toSnakeCase(input);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void toScreamingSnakeCaseShouldHandleNull() {
        // Given
        String input = null;

        // When
        String returnValue = CaseUtils.toScreamingSnakeCase(input);

        // Then
        assertThat(returnValue).isNull();
    }
    
    @Test
    public void toCamelCaseShouldHandleNull() {
        // Given
        String input = null;

        // When
        String returnValue = CaseUtils.toCamelCase(input);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void toPascalCaseShouldHandleNull() {
        // Given
        String input = null;

        // When
        String returnValue = CaseUtils.toPascalCase(input);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void toTitleCaseShouldHandleNull() {
        // Given
        String input = null;

        // When
        String returnValue = CaseUtils.toTitleCase(input);

        // Then
        assertThat(returnValue).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideSingleWords")
    public void toKebabCaseShouldConvertASingleWord(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toKebabCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one");
    }

    @ParameterizedTest
    @MethodSource("provideSingleWords")
    public void toSnakeCaseShouldConvertASingleWord(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one");
    }

    @ParameterizedTest
    @MethodSource("provideSingleWords")
    public void toScreamingSnakeCaseShouldConvertASingleWord(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toScreamingSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("ONE");
    }

    @ParameterizedTest
    @MethodSource("provideSingleWords")
    public void toCamelCaseShouldConvertASingleWord(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toCamelCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one");
    }

    @ParameterizedTest
    @MethodSource("provideSingleWords")
    public void toPascalCaseShouldConvertASingleWord(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toPascalCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One");
    }

    @ParameterizedTest
    @MethodSource("provideSingleWords")
    public void toTitleCaseShouldConvertASingleWord(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toTitleCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One");
    }

    @ParameterizedTest
    @MethodSource("provideValuesInMultipleFormats")
    public void toKebabCaseShouldConvertFromMultipleFormats(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toKebabCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one-two-three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesInMultipleFormats")
    public void toSnakeCaseShouldConvertFromMultipleFormats(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one_two_three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesInMultipleFormats")
    public void toScreamingSnakeCaseShouldConvertFromMultipleFormats(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toScreamingSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("ONE_TWO_THREE");
    }

    @ParameterizedTest
    @MethodSource("provideValuesInMultipleFormats")
    public void toCamelCaseShouldConvertFromMultipleFormats(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toCamelCase(input);

        // Then
        assertThat(returnValue).isEqualTo("oneTwoThree");
    }

    @ParameterizedTest
    @MethodSource("provideValuesInMultipleFormats")
    public void toPascalCaseShouldConvertFromMultipleFormats(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toPascalCase(input);

        // Then
        assertThat(returnValue).isEqualTo("OneTwoThree");
    }

    @ParameterizedTest
    @MethodSource("provideValuesInMultipleFormats")
    public void toTitleCaseShouldConvertFromMultipleFormats(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toTitleCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One Two Three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitSeparatedByDelimiters")
    public void toKebabCaseShouldHandleADigitSeparatedByDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toKebabCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one-2-three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitSeparatedByDelimiters")
    public void toSnakeCaseShouldHandleADigitSeparatedByDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one_2_three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitSeparatedByDelimiters")
    public void toScreamingSnakeCaseShouldHandleADigitSeparatedByDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toScreamingSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("ONE_2_THREE");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitSeparatedByDelimiters")
    public void toCamelCaseShouldHandleADigitSeparatedByDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toCamelCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one2Three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitSeparatedByDelimiters")
    public void toPascalCaseShouldHandleADigitSeparatedByDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toPascalCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One2Three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitSeparatedByDelimiters")
    public void toTitleCaseShouldHandleADigitSeparatedByDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toTitleCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One 2 Three");
    }

    // BOB

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitAfterALetter")
    public void toKebabCaseShouldHandleADigitAfterALetter(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toKebabCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one-v2-three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitAfterALetter")
    public void toSnakeCaseShouldHandleADigitAfterALetter(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one_v2_three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitAfterALetter")
    public void toScreamingSnakeCaseShouldHandleADigitAfterALetter(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toScreamingSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("ONE_V2_THREE");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitAfterALetter")
    public void toCamelCaseShouldHandleADigitAfterALetter(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toCamelCase(input);

        // Then
        assertThat(returnValue).isEqualTo("oneV2Three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitAfterALetter")
    public void toPascalCaseShouldHandleADigitAfterALetter(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toPascalCase(input);

        // Then
        assertThat(returnValue).isEqualTo("OneV2Three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithADigitAfterALetter")
    public void toTitleCaseShouldHandleADigitAfterALetter(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toTitleCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One V2 Three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleLeadingDelimiters")
    public void toKebabCaseShouldHandleMultipleLeadingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toKebabCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one-two-three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleLeadingDelimiters")
    public void toSnakeCaseShouldHandleMultipleLeadingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one_two_three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleLeadingDelimiters")
    public void toScreamingSnakeCaseShouldHandleMultipleLeadingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toScreamingSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("ONE_TWO_THREE");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleLeadingDelimiters")
    public void toCamelCaseShouldHandleMultipleLeadingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toCamelCase(input);

        // Then
        assertThat(returnValue).isEqualTo("oneTwoThree");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleLeadingDelimiters")
    public void toPascalCaseShouldHandleMultipleLeadingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toPascalCase(input);

        // Then
        assertThat(returnValue).isEqualTo("OneTwoThree");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleLeadingDelimiters")
    public void toTitleCaseShouldHandleMultipleLeadingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toTitleCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One Two Three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleTrailingDelimiters")
    public void toKebabCaseShouldHandleMultipleTrailingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toKebabCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one-two-three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleTrailingDelimiters")
    public void toSnakeCaseShouldHandleMultipleTrailingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one_two_three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleTrailingDelimiters")
    public void toScreamingSnakeCaseShouldHandleMultipleTrailingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toScreamingSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("ONE_TWO_THREE");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleTrailingDelimiters")
    public void toCamelCaseShouldHandleMultipleTrailingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toCamelCase(input);

        // Then
        assertThat(returnValue).isEqualTo("oneTwoThree");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleTrailingDelimiters")
    public void toPascalCaseShouldHandleMultipleTrailingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toPascalCase(input);

        // Then
        assertThat(returnValue).isEqualTo("OneTwoThree");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleTrailingDelimiters")
    public void toTitleCaseShouldHandleMultipleTrailingDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toTitleCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One Two Three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleEmbeddedDelimiters")
    public void toKebabCaseShouldHandleMultipleEmbeddedDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toKebabCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one-two-three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleEmbeddedDelimiters")
    public void toSnakeCaseShouldHandleMultipleEmbeddedDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("one_two_three");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleEmbeddedDelimiters")
    public void toScreamingSnakeCaseShouldHandleMultipleEmbeddedDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toScreamingSnakeCase(input);

        // Then
        assertThat(returnValue).isEqualTo("ONE_TWO_THREE");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleEmbeddedDelimiters")
    public void toCamelCaseShouldHandleMultipleEmbeddedDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toCamelCase(input);

        // Then
        assertThat(returnValue).isEqualTo("oneTwoThree");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleEmbeddedDelimiters")
    public void toPascalCaseShouldHandleMultipleEmbeddedDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toPascalCase(input);

        // Then
        assertThat(returnValue).isEqualTo("OneTwoThree");
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithMultipleEmbeddedDelimiters")
    public void toTitleCaseShouldHandleMultipleEmbeddedDelimiters(String input) {
        // Given
        log.info("Input: \"{}\"", StringEscapeUtils.escapeString(input));

        // When
        String returnValue = CaseUtils.toTitleCase(input);

        // Then
        assertThat(returnValue).isEqualTo("One Two Three");
    }

    private static List<String> provideSingleWords() {
        return List.of("one", "One", "ONE");
    }
    
    private static List<String> provideValuesInMultipleFormats() {
        return List.of("oneTwoThree", "OneTwoThree", "one_two_three", "one-two-three", "ONE_TWO_THREE", "one two three", "ONE TWO THREE");
    }

    private static List<String> provideValuesWithADigitSeparatedByDelimiters() {
        return List.of("one_2_three", "one-2-three", "ONE_2_THREE", "one 2 three", "ONE 2 THREE");
    }

    private static List<String> provideValuesWithADigitAfterALetter() {
        return List.of("oneV2Three", "OneV2Three", "one_v2_three", "one-v2-three", "ONE_V2_THREE", "one v2 three", "ONE V2 THREE");
    }

    private static List<String> provideValuesWithMultipleLeadingDelimiters() {
        return List.of("__one_two_three", "--one-two-three", "__ONE_TWO_THREE", "  one two three", "  ONE TWO THREE");
    }

    private static List<String> provideValuesWithMultipleTrailingDelimiters() {
        return List.of("one_two_three__", "one-two-three--", "ONE_TWO_THREE__", "one two three  ", "ONE TWO THREE  ");
    }

    private static List<String> provideValuesWithMultipleEmbeddedDelimiters() {
        return List.of("one__two_three", "one--two-three", "ONE__TWO_THREE", "one  two three", "ONE  TWO THREE");
    }
}
