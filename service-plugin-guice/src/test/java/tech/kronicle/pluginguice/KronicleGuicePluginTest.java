package tech.kronicle.pluginguice;

import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.testutils.TestConfig;
import tech.kronicle.pluginguice.testutils.TestKronicleGuicePlugin;
import tech.kronicle.pluginguice.testutils.TestPluginManager;

import static org.assertj.core.api.Assertions.assertThat;

public class KronicleGuicePluginTest {

    @Test
    public void initializeShouldCreateTheGuiceInjector() {
        // Given
        KronicleGuicePlugin underTest = new TestKronicleGuicePlugin(new PluginWrapper(
                new TestPluginManager(),
                null,
                null,
                null
        ));
        TestConfig config = new TestConfig();

        // When
        underTest.initialize(config);
        Injector returnValue = underTest.getGuiceInjector();

        // Then
        assertThat(returnValue).isNotNull();

        // When
        Object configReturnValue = returnValue.getInstance(TestConfig.class);

        // Then
        assertThat(configReturnValue).isEqualTo(config);
    }

}
