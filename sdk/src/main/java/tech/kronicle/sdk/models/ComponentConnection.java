package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static java.util.Objects.nonNull;
import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class ComponentConnection implements ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    @JsonAlias("target")
    String targetComponentId;
    @Pattern(regexp = PatternStrings.ID)
    String type;
    String label;
    String description;
    List<@Valid Tag> tags;

    public ComponentConnection(
            String targetComponentId,
            String type, String label,
            String description,
            List<@Valid Tag> tags
    ) {
        this.targetComponentId = targetComponentId;
        this.type = type;
        this.label = label;
        this.description = description;
        this.tags = createUnmodifiableList(tags);
    }

    @Override
    public String reference() {
        return "==" + (nonNull(type) ? type : "") + "==> " + targetComponentId;
    }
}
