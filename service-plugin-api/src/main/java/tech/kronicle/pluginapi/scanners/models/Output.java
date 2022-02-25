package tech.kronicle.pluginapi.scanners.models;

import lombok.Value;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ScannerError;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

@Value
public class Output<O> {

    O output;
    UnaryOperator<Component> componentTransformer;
    @NotNull
    List<ScannerError> errors;

    private Output(UnaryOperator<Component> componentTransformer) {
        requireNonNull(componentTransformer, "componentTransformer");
        this.componentTransformer = componentTransformer;
        this.output = null;
        this.errors = List.of();
    }

    private Output(UnaryOperator<Component> componentTransformer, ScannerError error) {
        requireNonNull(componentTransformer, "componentTransformer");
        requireNonNull(error, "error");
        this.componentTransformer = componentTransformer;
        this.output = null;
        this.errors = List.of(error);
    }

    private Output(UnaryOperator<Component> componentTransformer, List<ScannerError> errors) {
        requireNonNull(componentTransformer, "componentTransformer");
        requireNonNull(errors, "errors");
        this.componentTransformer = componentTransformer;
        this.output = null;
        this.errors = List.copyOf(errors);
    }

    private Output(UnaryOperator<Component> componentTransformer, O output) {
        requireNonNull(componentTransformer, "componentTransformer");
        requireNonNull(output, "output");
        this.componentTransformer = componentTransformer;
        this.output = output;
        this.errors = List.of();
    }

    private Output(UnaryOperator<Component> componentTransformer, O output, ScannerError error) {
        requireNonNull(componentTransformer, "componentTransformer");
        requireNonNull(output, "output");
        requireNonNull(error, "error");
        this.componentTransformer = componentTransformer;
        this.output = output;
        this.errors = List.of(error);
    }

    private Output(UnaryOperator<Component> componentTransformer, O output, List<ScannerError> errors) {
        requireNonNull(componentTransformer, "componentTransformer");
        requireNonNull(output, "output");
        requireNonNull(errors, "errors");
        this.componentTransformer = componentTransformer;
        this.output = output;
        this.errors = List.copyOf(errors);
    }

    private Output(ScannerError error) {
        requireNonNull(error, "error");
        this.componentTransformer = null;
        this.output = null;
        this.errors = List.of(error);
    }

    private Output(List<ScannerError> errors) {
        requireNonNull(errors, "errors");
        this.componentTransformer = null;
        this.output = null;
        this.errors = List.copyOf(errors);
    }

    public static <O> Output<O> of(UnaryOperator<Component> componentTransformer) {
        return new Output<>(componentTransformer);
    }

    public static <O> Output<O> of(UnaryOperator<Component> componentTransformer, ScannerError error) {
        return new Output<>(componentTransformer, error);
    }

    public static <O> Output<O> of(UnaryOperator<Component> componentTransformer, List<ScannerError> errors) {
        return new Output<>(componentTransformer, errors);
    }

    public static <O> Output<O> of(UnaryOperator<Component> componentTransformer, O output) {
        return new Output<>(componentTransformer, output);
    }

    public static <O> Output<O> of(UnaryOperator<Component> componentTransformer, O output, ScannerError error) {
        return new Output<>(componentTransformer, output, error);
    }

    public static <O> Output<O> of(UnaryOperator<Component> componentTransformer, O output, List<ScannerError> errors) {
        return new Output<>(componentTransformer, output, errors);
    }

    public static <O> Output<O> of(ScannerError error) {
        return new Output<>(error);
    }

    public static <O> Output<O> of(List<ScannerError> errors) {
        return new Output<>(errors);
    }

    public boolean success() {
        return errors.isEmpty();
    }

    public boolean failed() {
        return !success();
    }
}
