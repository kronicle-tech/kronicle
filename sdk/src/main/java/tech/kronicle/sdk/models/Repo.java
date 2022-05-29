package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

@Value
@With
@Builder(toBuilder = true)
public class Repo implements ObjectWithReference {

    @NotBlank
    String url;
    String description;
    String defaultBranch;
    @NotNull
    Boolean hasComponentMetadataFile;
    @Valid
    List<ComponentState> states;

    public Repo(
            String url,
            String description,
            String defaultBranch,
            Boolean hasComponentMetadataFile,
            List<ComponentState> states
    ) {
        this.url = url;
        this.description = description;
        this.defaultBranch = defaultBranch;
        this.hasComponentMetadataFile = hasComponentMetadataFile;
        this.states = createUnmodifiableList(states);
    }

    @Override
    public String reference() {
        return url;
    }

    public Repo addStates(List<ComponentState> states) {
        return withStates(
                unmodifiableUnionOfLists(List.of(this.states, states))
        );
    }
}
