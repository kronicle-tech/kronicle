package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Diagram implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    @NotBlank
    String name;
    Boolean discovered;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String type;
    String description;
    List<@Valid Tag> tags;
    List<@Valid DiagramConnection> connections;

    List<@Valid DiagramState> states;

    public Diagram(
            String id,
            String name,
            Boolean discovered,
            String type,
            String description,
            List<@Valid Tag> tags,
            List<@Valid DiagramConnection> connections,
            List<@Valid DiagramState> states
    ) {
        this.id = id;
        this.name = name;
        this.discovered = discovered;
        this.type = type;
        this.description = description;
        this.tags = createUnmodifiableList(tags);
        this.connections = createUnmodifiableList(connections);
        this.states = createUnmodifiableList(states);
    }

    @Override
    public String reference() {
        return id;
    }

    public Diagram addState(DiagramState state) {
        return withStates(
                unmodifiableUnionOfLists(List.of(this.states, List.of(state)))
        );
    }

    public Diagram addStates(List<DiagramState> states) {
        return withStates(
                unmodifiableUnionOfLists(List.of(this.states, states))
        );
    }

    @JsonIgnore
    public <T extends DiagramState> List<T> getStates(String type) {
        return states.stream()
                .filter(state -> Objects.equals(state.getType(), type))
                .map(state -> (T) state)
                .collect(toUnmodifiableList());
    }

    @JsonIgnore
    public <T extends DiagramState> T getState(String type) {
        List<DiagramState> matches = getStates(type);
        if (matches.size() > 1) {
            throw new IllegalArgumentException("There are more than 1 states with type \"" + type + "\"");
        } else if (matches.isEmpty()) {
            return null;
        } else {
            return (T) matches.get(0);
        }
    }
}
