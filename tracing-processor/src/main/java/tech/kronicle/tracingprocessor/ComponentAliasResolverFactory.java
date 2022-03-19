package tech.kronicle.tracingprocessor;

public final class ComponentAliasResolverFactory {

    public static ComponentAliasResolver createComponentAliasResolver() {
        return new ComponentAliasResolver();
    }

    private ComponentAliasResolverFactory() {
    }
}
