package tech.kronicle.pluginapi.scanners.models;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ScannerError;
import lombok.Value;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class OutputTest {

    private final UnaryOperator<Component> componentTransformer = component -> component;
    private final TestOutput output = new TestOutput("output");
    private final ScannerError error = new ScannerError("test_scanner", "test_message", null);
    private final List<ScannerError> errors = List.of(error);

    @Test
    public void ofWithComponentTransformerShouldSetComponentTransformer() {
        // When
        Output<TestOutput> underTest = Output.of(componentTransformer);

        // Then
        assertThat(underTest.getComponentTransformer()).isSameAs(componentTransformer);
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).isEmpty();
    }

    @Test
    public void ofWithComponentTransformerShouldCheckComponentTransformerIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of((UnaryOperator<Component>) null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("componentTransformer");
    }

    @Test
    public void ofWithComponentTransformerAndErrorShouldSetComponentTransformerAndError() {
        // When
        Output<TestOutput> underTest = Output.of(componentTransformer, error);

        // Then
        assertThat(underTest.getComponentTransformer()).isSameAs(componentTransformer);
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).containsExactly(error);
        assertIsUnmodifiable(underTest.getErrors());
    }

    @Test
    public void ofWithComponentTransformerAndErrorShouldCheckComponentTransformerIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(null, error));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("componentTransformer");
    }

    @Test
    public void ofWithComponentTransformerAndErrorShouldCheckErrorIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(componentTransformer, (ScannerError) null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("error");
    }

    @Test
    public void ofWithComponentTransformerAndErrorsShouldSetComponentTransformerAndErrors() {
        // When
        Output<TestOutput> underTest = Output.of(componentTransformer, errors);

        // Then
        assertThat(underTest.getComponentTransformer()).isSameAs(componentTransformer);
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).containsExactlyElementsOf(errors);
        assertIsUnmodifiable(underTest.getErrors());
    }

    @Test
    public void ofWithComponentTransformerAndErrorsShouldCheckComponentTransformerIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(null, List.of(error)));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("componentTransformer");
    }

    @Test
    public void ofWithComponentTransformerAndErrorsShouldCheckErrorsIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(componentTransformer, (List<ScannerError>) null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("errors");
    }
    
    // Here
    
    @Test
    public void ofWithComponentTransformerAndOutputShouldSetComponentTransformerAndOutput() {
        // When
        Output<TestOutput> underTest = Output.of(componentTransformer, output);

        // Then
        assertThat(underTest.getComponentTransformer()).isSameAs(componentTransformer);
        assertThat(underTest.getOutput()).isSameAs(output);
        assertThat(underTest.getErrors()).isEmpty();
    }

    @Test
    public void ofWithComponentTransformerAndOutputShouldCheckComponentTransformerIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(null, output));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("componentTransformer");
    }

    @Test
    public void ofWithComponentTransformerAndOutputShouldCheckOutputIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(componentTransformer, (TestOutput) null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("output");
    }

    @Test
    public void ofWithComponentTransformerAndOutputAndErrorShouldSetComponentTransformerAndOutputAndError() {
        // When
        Output<TestOutput> underTest = Output.of(componentTransformer, output, error);

        // Then
        assertThat(underTest.getComponentTransformer()).isSameAs(componentTransformer);
        assertThat(underTest.getOutput()).isSameAs(output);
        assertThat(underTest.getErrors()).containsExactly(error);
        assertIsUnmodifiable(underTest.getErrors());
    }

    @Test
    public void ofWithComponentTransformerAndOutputAndErrorShouldCheckComponentTransformerIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(null, output, error));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("componentTransformer");
    }

    @Test
    public void ofWithComponentTransformerAndOutputAndErrorShouldCheckOutputIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(componentTransformer, null, error));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("output");
    }

    @Test
    public void ofWithComponentTransformerAndOutputAndErrorShouldCheckErrorIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(componentTransformer, output, (ScannerError) null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("error");
    }

    @Test
    public void ofWithComponentTransformerAndOutputAndErrorsShouldSetComponentTransformerAndOutputAndErrors() {
        // When
        Output<TestOutput> underTest = Output.of(componentTransformer, output, errors);

        // Then
        assertThat(underTest.getComponentTransformer()).isSameAs(componentTransformer);
        assertThat(underTest.getOutput()).isSameAs(output);
        assertThat(underTest.getErrors()).containsExactlyElementsOf(errors);
        assertIsUnmodifiable(underTest.getErrors());
    }

    @Test
    public void ofWithComponentTransformerAndOutputAndErrorsShouldCheckComponentTransformerIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(null, output, List.of(error)));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("componentTransformer");
    }

    @Test
    public void ofWithComponentTransformerAndOutputAndErrorsShouldCheckOutputIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(componentTransformer, null, List.of(error)));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("output");
    }

    @Test
    public void ofWithComponentTransformerAndOutputAndErrorsShouldCheckErrorsIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of(componentTransformer, output, (List<ScannerError>) null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("errors");
    }

    @Test
    public void ofWithErrorShouldSetError() {
        // When
        Output<Void> underTest = Output.of(errors);

        // Then
        assertThat(underTest.getComponentTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).containsExactlyElementsOf(errors);
        assertIsUnmodifiable(underTest.getErrors());
    }

    @Test
    public void ofWithErrorShouldCheckErrorIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of((ScannerError) null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("error");
    }

    @Test
    public void ofWithErrorsShouldSetErrors() {
        // When
        Output<Void> underTest = Output.of(error);

        // Then
        assertThat(underTest.getComponentTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.getErrors()).containsExactly(error);
        assertIsUnmodifiable(underTest.getErrors());
    }

    @Test
    public void ofWithErrorsShouldCheckErrorsIsNotNull() {
        // When
        Throwable thrown = catchThrowable(() -> Output.of((List<ScannerError>) null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("errors");
    }

    @Test
    public void successReturnsTrueAndFailedReturnsFalseWhenComponentTransformerIsNullAndOutputIsNullAndErrorsIsEmpty() {
        // When
        Output<Void> underTest = Output.of(List.of());

        // Then
        assertThat(underTest.getComponentTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.success()).isTrue();
        assertThat(underTest.failed()).isFalse();
    }

    @Test
    public void successReturnsTrueAndFailedReturnsFalseWhenComponentTransformerIsNonNullAndOutputIsNonNullAndErrorsIsEmpty() {
        // When
        Output<TestOutput> underTest = Output.of(componentTransformer, output, List.of());

        // Then
        assertThat(underTest.getComponentTransformer()).isSameAs(componentTransformer);
        assertThat(underTest.getOutput()).isSameAs(output);
        assertThat(underTest.success()).isTrue();
        assertThat(underTest.failed()).isFalse();
    }

    @Test
    public void successReturnsFalseAndFailedReturnsTrueWhenComponentTransformerIsNullAndOutputIsNullAndErrorsIsNonEmpty() {
        // When
        Output<Void> underTest = Output.of(errors);

        // Then
        assertThat(underTest.getComponentTransformer()).isNull();
        assertThat(underTest.getOutput()).isNull();
        assertThat(underTest.success()).isFalse();
        assertThat(underTest.failed()).isTrue();
    }

    @Test
    public void successReturnsFalseAndFailedReturnsTrueWhenComponentTransformerIsNonNullAndOutputIsNonNullAndErrorsIsEmpty() {
        // When
        Output<TestOutput> underTest = Output.of(componentTransformer, output, errors);

        // Then
        assertThat(underTest.getComponentTransformer()).isSameAs(componentTransformer);
        assertThat(underTest.getOutput()).isSameAs(output);
        assertThat(underTest.success()).isFalse();
        assertThat(underTest.failed()).isTrue();
    }

    private void assertIsUnmodifiable(@NotNull List<ScannerError> value) {
        Throwable thrown = catchThrowable(() -> value.add(new ScannerError("test_scanner", "test_message", null)));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Value
    private static class TestOutput {

        String value;
    }
}
