package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Doc {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    String dir;
    String file;
    @NotBlank
    String name;
    String description;
    String notes;
    List<@Valid Tag> tags;

    public Doc(
            String id,
            String dir,
            String file,
            String name,
            String description,
            String notes,
            List<@Valid Tag> tags
    ) {
        this.id = id;
        this.dir = dir;
        this.file = file;
        this.name = name;
        this.description = description;
        this.notes = notes;
        this.tags = createUnmodifiableList(tags);
    }
}
