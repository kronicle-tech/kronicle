package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class TechDebt {

    @NotBlank
    String description;
    String notes;
    Priority priority;
    List<@Valid Link> links;

    public TechDebt(String description, String notes, Priority priority, List<Link> links) {
        this.description = description;
        this.notes = notes;
        this.priority = priority;
        this.links = createUnmodifiableList(links);
    }
}
