package tech.kronicle.service.scanners;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.service.BaseTest;
import tech.kronicle.service.scanners.models.Output;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseScannerTest extends BaseTest {

    protected <T> Component getMutatedComponent(Output<T> output) {
        assertThat(output.getErrors()).isNotNull();

        if (output.getErrors().size() > 0) {
            throw new RuntimeException(output.getErrors().get(0).toString());
        }

        return getMutatedComponentIgnoringErrors(output);
    }

    protected <T> Component getMutatedComponentIgnoringErrors(Output<T> output) {
        return output.getComponentTransformer().apply(Component.builder().build());
    }
}