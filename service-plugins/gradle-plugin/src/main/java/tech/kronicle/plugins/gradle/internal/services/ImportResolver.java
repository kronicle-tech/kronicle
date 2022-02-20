package tech.kronicle.plugins.gradle.internal.services;

import tech.kronicle.plugins.gradle.internal.models.Import;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.Objects;
import java.util.Set;

@SpringComponent
public class ImportResolver {

    public Import importResolver(String value, Set<Import> imports) {
        return imports.stream()
                .filter(item -> Objects.equals(item.getAliasName(), value))
                .findFirst().orElse(null);
    }
}
