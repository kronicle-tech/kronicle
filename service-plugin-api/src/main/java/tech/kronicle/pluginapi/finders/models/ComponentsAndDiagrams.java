package tech.kronicle.pluginapi.finders.models;

import lombok.Builder;
import lombok.Value;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Diagram;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@Builder(toBuilder = true)
public class ComponentsAndDiagrams {

    public static ComponentsAndDiagrams EMPTY = new ComponentsAndDiagrams(List.of(), List.of());

    List<Component> components;
    List<Diagram> diagrams;

    public ComponentsAndDiagrams(List<Component> components, List<Diagram> diagrams) {
        this.components = createUnmodifiableList(components);
        this.diagrams = createUnmodifiableList(diagrams);
    }
}
