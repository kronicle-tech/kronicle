package tech.kronicle.pluginapi.scanners.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import tech.kronicle.sdk.models.ScannerError;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class Output<O, T> {

    O output;
    UnaryOperator<T> transformer;
    @NotNull
    List<ScannerError> errors;
    Duration cacheTtl;

    public Output(O output, UnaryOperator<T> transformer, List<ScannerError> errors, Duration cacheTtl) {
        this.output = output;
        this.transformer = transformer;
        this.errors = createUnmodifiableList(errors);
        this.cacheTtl = cacheTtl;
    }

    public static <O> Output<O, Void> ofOutput(O output, Duration cacheTtl) {
        return new Output<>(output, null, null, cacheTtl);
    }

    public static <O, T> Output<O, T> ofTransformer(UnaryOperator<T> transformer, Duration cacheTtl) {
        return new Output<>(null, transformer, null, cacheTtl);
    }

    public static <O, T> Output<O, T> ofErrors(List<ScannerError> errors, Duration cacheTtl) {
        requireNonNull(errors, "errors");
        return new Output<>(null, null, errors, cacheTtl);
    }

    public static <O, T> Output<O, T> ofError(ScannerError error, Duration cacheTtl) {
        requireNonNull(error, "error");
        return new Output<>(null, null, List.of(error), cacheTtl);
    }

    public static <O, T> Output<O, T> empty(Duration cacheTtl) {
        return new Output<>(null, null, null, cacheTtl);
    }

    public static OutputBuilder<Void, Void> builder(Duration cacheTtl) {
        return new OutputBuilder<>(cacheTtl);
    }

    public boolean success() {
        return errors.isEmpty();
    }

    public boolean failed() {
        return !success();
    }

    public boolean hasOutput() {
        return nonNull(output);
    }

    public <O2> Optional<O2> mapOutput(Function<O, O2> mapper) {
        return Optional.ofNullable(output).map(mapper);
    }

    public O getOutputOrElse(O other) {
        return nonNull(output) ? output : other;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OutputBuilder<O, T> {

        private O output;
        private UnaryOperator<T> transformer;
        private List<ScannerError> errors = new ArrayList<>();
        private Duration cacheTtl;

        public OutputBuilder(Duration cacheTtl) {
            this.cacheTtl = cacheTtl;
        }

        public <O2> OutputBuilder<O2, T> output(O2 output) {
            return new OutputBuilder<>(output, transformer, errors, cacheTtl);
        }

        public <T2> OutputBuilder<O, T2> transformer(UnaryOperator<T2> transformer) {
            return new OutputBuilder<>(output, transformer, errors, cacheTtl);
        }

        public OutputBuilder<O, T> errors(List<ScannerError> errors) {
            requireNonNull(errors, "errors");
            this.errors = new ArrayList<>(errors);
            return this;
        }

        public OutputBuilder<O, T> error(ScannerError error) {
            requireNonNull(error, "error");
            this.errors.add(error);
            return this;
        }

        public Output<O, T> build() {
            return new Output<>(output, transformer, errors, cacheTtl);
        }
    }
}
