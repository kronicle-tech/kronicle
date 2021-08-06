package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.moneysupermarket.componentcatalog.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Area implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*")
    String id;
    @NotBlank
    String name;
    List<@NotBlank @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*") String> tags;
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
