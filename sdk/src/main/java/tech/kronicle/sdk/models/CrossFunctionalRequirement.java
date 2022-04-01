package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.utils.ListUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class CrossFunctionalRequirement {

    @NotBlank
    String description;
    String notes;
    List<@Valid Link> links;

    public CrossFunctionalRequirement(String description, String notes, List<Link> links) {
        this.description = description;
        this.notes = notes;
        this.links = createUnmodifiableList(links);
    }
}
