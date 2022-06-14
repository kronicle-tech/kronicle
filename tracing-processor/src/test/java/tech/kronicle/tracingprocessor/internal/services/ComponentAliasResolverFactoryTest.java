package tech.kronicle.tracingprocessor.internal.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.tracingprocessor.internal.services.ComponentAliasResolver;
import tech.kronicle.tracingprocessor.internal.services.ComponentAliasResolverFactory;

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
