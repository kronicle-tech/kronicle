package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.utils.ListUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Area implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    @NotBlank
    String name;
    List<@NotBlank @Pattern(regexp = PatternStrings.ID) String> tags;
    String description;
    String notes;
    List<@Valid Link> links;
    List<@Valid Team> teams;
    List<@Valid Component> components;

    public Area(String id, String name, List<String> tags, String description, String notes, List<Link> links, List<Team> teams,
            List<Component> components) {
        this.id = id;
        this.name = name;
        this.tags = createUnmodifiableList(tags);
        this.description = description;
        this.notes = notes;
        this.links = createUnmodifiableList(links);
        this.teams = createUnmodifiableList(teams);
        this.components = createUnmodifiableList(components);
    }

    @Override
    public String reference() {
        return id;
    }
}
