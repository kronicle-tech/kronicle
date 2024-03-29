package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.utils.ListUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Team implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    @NotBlank
    String name;
    @Email
    String emailAddress;
    @Pattern(regexp = PatternStrings.ID)
    String areaId;
    List<@NotBlank @Pattern(regexp = PatternStrings.ID) String> tags;
    String description;
    String notes;
    List<@Valid Link> links;
    List<@Valid Component> components;

    public Team(String id, String name, String emailAddress, String areaId, List<String> tags, String description, String notes, List<Link> links,
            List<Component> components) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.areaId = areaId;
        this.tags = createUnmodifiableList(tags);
        this.description = description;
        this.notes = notes;
        this.links = createUnmodifiableList(links);
        this.components = createUnmodifiableList(components);
    }

    @Override
    public String reference() {
        return id;
    }
}
