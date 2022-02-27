package tech.kronicle.plugins.gradle.internal.services;

import tech.kronicle.plugins.gradle.internal.models.Import;

import java.util.Objects;
import java.util.Set;

public class ImportResolver {

    public Import importResolver(String value, Set<Import> imports) {
        return imports.stream()
                .filter(item -> Objects.equals(item.getAliasName(), value))
                .findFirst().orElse(null);
    }
}
