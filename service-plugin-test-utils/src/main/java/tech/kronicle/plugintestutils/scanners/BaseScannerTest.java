package tech.kronicle.plugintestutils.scanners;

import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.testutils.BaseTest;
import tech.kronicle.sdk.models.Component;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class BaseScannerTest extends BaseTest {

    protected <T> Output<T, Component> maskTransformer(Output<T, Component> output) {
        return new Output<>(output.getOutput(), null, output.getErrors(), output.getCacheTtl());
    }

    protected <T> Component getMutatedComponent(Output<T, Component> output) {
        return getMutatedComponent(output, Component.builder().build());
    }

    protected <T> Component getMutatedComponent(Output<T, Component> output, Component component) {
        assertThat(output.getErrors()).isNotNull();

        if (output.getErrors().size() > 0) {
            throw new RuntimeException(output.getErrors().get(0).toString());
        }

        return getMutatedComponentIgnoringErrors(output, component);
    }

    protected <T> Component getMutatedComponentIgnoringErrors(Output<T, Component> output) {
        return getMutatedComponentIgnoringErrors(output, Component.builder().build());
    }

    protected <T> Component getMutatedComponentIgnoringErrors(Output<T, Component> output, Component component) {
        return nonNull(output.getTransformer()) ? output.getTransformer().apply(component) : component;
    }
}
