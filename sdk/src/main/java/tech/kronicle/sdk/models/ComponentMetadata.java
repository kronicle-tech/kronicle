package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class ComponentMetadata implements ObjectWithReference {

    List<@Valid ComponentType> componentTypes;
    List<@Valid Platform> platforms;
    List<@Valid Area> areas;
    List<@Valid Team> teams;
    List<@Valid Component> components;

    public ComponentMetadata(List<ComponentType> componentTypes, List<Platform> platforms, List<Area> areas, List<Team> teams, List<Component> components) {
        this.componentTypes = createUnmodifiableList(componentTypes);
        this.platforms = createUnmodifiableList(platforms);
        this.areas = createUnmodifiableList(areas);
        this.teams = createUnmodifiableList(teams);
        this.components = createUnmodifiableList(components);
    }

    @Override
    public String reference() {
        return "component-metadata";
    }
}
