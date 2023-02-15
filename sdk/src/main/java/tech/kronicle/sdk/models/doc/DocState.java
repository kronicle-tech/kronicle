package tech.kronicle.sdk.models.doc;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class DocState implements ComponentState {

    public static final String TYPE = "doc";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    String dir;
    String file;
    @NotBlank
    String name;
    String description;
    String notes;
    List<Tag> tags;
    @NotNull
    List<DocFile> files;

    public DocState(
            String pluginId,
            String id,
            String dir,
            String file,
            String name,
            String description,
            String notes,
            List<@Valid Tag> tags,
            List<@Valid DocFile> files
    ) {
        this.pluginId = pluginId;
        this.id = id;
        this.dir = dir;
        this.file = file;
        this.name = name;
        this.description = description;
        this.notes = notes;
        this.tags = createUnmodifiableList(tags);
        this.files = createUnmodifiableList(files);
    }
}
