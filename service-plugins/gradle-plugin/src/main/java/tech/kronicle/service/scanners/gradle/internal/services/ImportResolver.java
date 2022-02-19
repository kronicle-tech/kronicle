package tech.kronicle.service.scanners.gradle.internal.services;

import tech.kronicle.service.scanners.gradle.internal.models.Import;

import javax.inject.Singleton;
import java.util.Objects;
import java.util.Set;

@Singleton
public class ImportResolver {

    public Import importResolver(String value, Set<Import> imports) {
        return imports.stream()
                .filter(item -> Objects.equals(item.getAliasName(), value))
                .findFirst().orElse(null);
    }
}
