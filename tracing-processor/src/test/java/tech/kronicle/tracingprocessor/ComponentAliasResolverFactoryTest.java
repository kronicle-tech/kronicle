package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentAliasResolverFactoryTest {

    @Test
    public void createComponentAliasResolver() {
        // When
        ComponentAliasResolver componentAliasResolver = ComponentAliasResolverFactory.createComponentAliasResolver();

        // Then
        assertThat(componentAliasResolver).isNotNull();
    }
}
