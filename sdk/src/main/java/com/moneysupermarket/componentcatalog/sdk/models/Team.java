package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.moneysupermarket.componentcatalog.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Team implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*")
    String id;
    @NotBlank
    String name;
    @Email
    String emailAddress;
    @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*")
    String areaId;
    List<@NotBlank @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*") String> tags;
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
