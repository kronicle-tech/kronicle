package tech.kronicle.pluginapi.scanners.models;

import lombok.Value;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ScannerError;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class OutputTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final UnaryOperator<Component> transformer = component -> component;
    private final TestOutput output = new TestOutput("output");
    private final ScannerError error1 = createError(1);
    private final ScannerError error2 = createError(2);
    private final ScannerError error3 = createError(3);
    private final ScannerError error4 = createError(4);
    private final List<ScannerError> errors = List.of(error3, error4);

    @Test
    public void ofOutputShouldSetOutput() {
        // When
        Output<TestOutput, Void> underTest = Output.ofOutput(output, CACHE_TTL);

        // Then
        assertThat(underTest.getTransformer()).isNull();
        assertThat(underTest.getOutput()).isSameAs(output);
        assertThat(underTest.getErrors()).isEmpty();
        assertThat(underTest.success()).isTrue();
        assertThat(underTest.failed()).isFalse();
        assertThat(underTest.hasOutput()).isTrue();
        assertThat(underTest.getCacheTtl()).isEqualTo(CACHE_TTL);
    }

    @Test
    public void ofOutputShouldAcceptANullOutput() {
        // When
        Output<TestOutput, Void> underTest = Output.ofOutput(null, CACHE_TTL);

        // Then
        assertThat(underTest.getTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).isEmpty();
        assertThat(underTest.success()).isTrue();
        assertThat(underTest.failed()).isFalse();
        assertThat(underTest.hasOutput()).isFalse();
        assertThat(underTest.getCacheTtl()).isEqualTo(CACHE_TTL);
    }

    @Test
    public void ofTransformerShouldSetTransformer() {
        // When
        Output<TestOutput, Component> underTest = Output.ofTransformer(transformer, CACHE_TTL);

        // Then
        assertThat(underTest.getTransformer()).isSameAs(transformer);
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).isEmpty();
        assertThat(underTest.success()).isTrue();
        assertThat(underTest.failed()).isFalse();
        assertThat(underTest.hasOutput()).isFalse();
        assertThat(underTest.getCacheTtl()).isEqualTo(CACHE_TTL);
    }

    @Test
    public void ofTransformerShouldAcceptANullOutput() {
        // When
        Output<TestOutput, Component> underTest = Output.ofTransformer(null, CACHE_TTL);

        // Then
        assertThat(underTest.getTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).isEmpty();
        assertThat(underTest.success()).isTrue();
        assertThat(underTest.failed()).isFalse();
        assertThat(underTest.hasOutput()).isFalse();
        assertThat(underTest.getCacheTtl()).isEqualTo(CACHE_TTL);
    }

    @Test
    public void ofErrorShouldSetError() {
        // When
        Output<Void, Component> underTest = Output.ofError(error1, CACHE_TTL);

        // Then
        assertThat(underTest.getTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).containsExactly(error1);
        assertIsUnmodifiable(underTest.getErrors());
        assertThat(underTest.success()).isFalse();
        assertThat(underTest.failed()).isTrue();
        assertThat(underTest.hasOutput()).isFalse();
        assertThat(underTest.getCacheTtl()).isEqualTo(CACHE_TTL);
    }

    @Test
    public void ofErrorShouldCheckErrorIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.ofError(null, CACHE_TTL));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("error");
    }

    @Test
    public void ofErrorsShouldSetErrors() {
        // When
        Output<Void, Component> underTest = Output.ofErrors(errors, CACHE_TTL);

        // Then
        assertThat(underTest.getTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).containsExactly(error3, error4);
        assertIsUnmodifiable(underTest.getErrors());
        assertThat(underTest.success()).isFalse();
        assertThat(underTest.failed()).isTrue();
        assertThat(underTest.hasOutput()).isFalse();
        assertThat(underTest.getCacheTtl()).isEqualTo(CACHE_TTL);
    }

    @Test
    public void ofErrorsShouldCheckErrorsIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.ofErrors(null, CACHE_TTL));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("errors");
    }

    @Test
    public void emptyShouldSetNothing() {
        // When
        Output<Void, Component> underTest = Output.empty(CACHE_TTL);

        // Then
        assertThat(underTest.getTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.success()).isTrue();
        assertThat(underTest.failed()).isFalse();
        assertThat(underTest.hasOutput()).isFalse();
        assertThat(underTest.getCacheTtl()).isEqualTo(CACHE_TTL);
    }

    @Test
    public void builderOutputShouldSetOutput() {
        // When
        Output<TestOutput, Void> returnValue = Output.builder(CACHE_TTL)
                .output(output)
                .build();

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(output, null, null, CACHE_TTL));
    }

    @Test
    public void builderTransformerShouldSetTransformer() {
        // When
        Output<Void, Component> returnValue = Output.builder(CACHE_TTL)
                .transformer(transformer)
                .build();

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(null, transformer, null, CACHE_TTL));
    }

    @Test
    public void builderErrorsShouldSetErrors() {
        // When
        Output<Void, Void> returnValue = Output.builder(CACHE_TTL)
                .errors(errors)
                .build();

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(null, null, errors, CACHE_TTL));
    }

    @Test
    public void builderErrorShouldAddTheErrorToErrors() {
        // When
        Output<Void, Void> returnValue = Output.builder(CACHE_TTL)
                .error(error1)
                .build();

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(null, null, List.of(error1), CACHE_TTL));
    }

    @Test
    public void builderErrorWhenCalledMultipleTimesShouldAddTheErrorsToErrors() {
        // When
        Output<Void, Void> returnValue = Output.builder(CACHE_TTL)
                .error(error1)
                .error(error2)
                .build();

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(null, null, List.of(error1, error2), CACHE_TTL));
    }

    @Test
    public void builderShouldSetBeAbleToSetOutputAndTransformerAndErrors() {
        // When
        Output<TestOutput, Component> returnValue = Output.builder(CACHE_TTL)
                .output(output)
                .transformer(transformer)
                .errors(errors)
                .build();

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(
                output,
                transformer,
                errors,
                CACHE_TTL
        ));
    }

    @Test
    public void builderErrorWhenCalledMultipleTimesAndThereAreAlreadyErrorsShouldAddTheErrorsToErrors() {
        // When
        Output<Void, Void> returnValue = Output.builder(CACHE_TTL)
                .errors(errors)
                .error(error1)
                .error(error2)
                .build();

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(null, null, List.of(error3, error4, error1, error2), CACHE_TTL));
    }

    @Test
    public void mapOutputWhenOutputIsNullShouldReturnAnEmptyOptional() {
        // Given
        Output<String, Void> underTest = Output.ofOutput(null, CACHE_TTL);

        // When
        Optional<String> returnValue = underTest.mapOutput(value -> value + " mapped");

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void mapOutputWhenOutputPresentShouldMapTheOutput() {
        // Given
        Output<String, Void> underTest = Output.ofOutput("test-value", CACHE_TTL);

        // When
        Optional<String> returnValue = underTest.mapOutput(value -> value + " mapped");

        // Then
        assertThat(returnValue).hasValue("test-value mapped");
    }

    @Test
    public void getOutputOrElseWhenOutputIsNullShouldReturnOtherValue() {
        // Given
        Output<String, Void> underTest = Output.ofOutput(null, CACHE_TTL);

        // When
        String returnValue = underTest.getOutputOrElse("other-value");

        // Then
        assertThat(returnValue).isEqualTo("other-value");
    }

    @Test
    public void getOutputOrElseWhenOutputPresentShouldReturnOutput() {
        // Given
        Output<String, Void> underTest = Output.ofOutput("test-value", CACHE_TTL);

        // When
        String returnValue = underTest.getOutputOrElse("other-value");

        // Then
        assertThat(returnValue).isEqualTo("test-value");
    }

    private ScannerError createError(int errorNumber) {
        return new ScannerError(
                "test-scanner-id-" + errorNumber,
                "test-message-" + errorNumber,
                null
        );
    }

    private void assertIsUnmodifiable(@NotNull List<ScannerError> value) {
        Throwable thrown = catchThrowable(() -> value.add(createError(1)));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Value
    private static class TestOutput {

        String value;
    }
}
