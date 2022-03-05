package tech.kronicle.utils;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Import;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareRepository;

import java.util.Comparator;

public final class Comparators {

    public static final Comparator<Component> COMPONENTS = Comparator
            .comparing(Component::getTypeId, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Component::getName, Comparator.nullsLast(Comparator.naturalOrder()));

    public static final Comparator<Import> IMPORTS = Comparator
            .comparing(Import::getType, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Import::getName, Comparator.nullsLast(Comparator.naturalOrder()));

    public static final Comparator<SoftwareRepository> SOFTWARE_REPOSITORIES = Comparator
            .comparing(SoftwareRepository::getType, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(SoftwareRepository::getUrl, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(SoftwareRepository::getSafe, Comparator.nullsLast(Comparator.naturalOrder()));

    public static final Comparator<Software> SOFTWARE = Comparator
            .comparing(Software::getType, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Software::getName, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Software::getVersion, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Software::getPackaging, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Software::getScope, Comparator.nullsLast(Comparator.naturalOrder()));

    private Comparators() {
    }
}
