package tech.kronicle.tracingprocessor.internal.services;

public final class ComponentAliasResolverFactory {

    public static ComponentAliasResolver createComponentAliasResolver() {
        return new ComponentAliasResolver();
    }

    private ComponentAliasResolverFactory() {
    }
}
