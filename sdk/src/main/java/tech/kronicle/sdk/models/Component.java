package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.UniqueElements;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.jackson.TagOrStringDeserializer;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;

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
public class Component implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    @UniqueElements
    List<Alias> aliases;
    @NotBlank
    String name;
    Boolean discovered;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    @JsonAlias("typeId")
    String type;
    @JsonDeserialize(contentUsing = TagOrStringDeserializer.class)
    List<@Valid Tag> tags;
    @Valid
    RepoReference repo;
    String description;
    List<@Valid Responsibility> responsibilities;
    String notes;
    List<@Valid Link> links;
    List<@Valid ComponentTeam> teams;
    @Pattern(regexp = PatternStrings.ID)
    @JsonAlias("platform")
    String platformId;
    List<@Valid ComponentConnection> connections;
    @Deprecated
    List<@Valid ComponentDependency> dependencies;
    List<@Valid CrossFunctionalRequirement> crossFunctionalRequirements;
    List<@Valid TechDebt> techDebts;
    List<@Valid Doc> docs;
    List<@Valid OpenApiSpec> openApiSpecs;
    List<@Valid GraphQlSchema> graphQlSchemas;

    List<@Valid ComponentState> states;
    
    List<@Valid ScannerError> scannerErrors;
    List<@Valid TestResult> testResults;

    public Component(
            String id,
            List<Alias> aliases,
            String name,
            Boolean discovered,
            String type,
            List<Tag> tags,
            RepoReference repo,
            String description,
            List<Responsibility> responsibilities,
            String notes,
            List<Link> links,
            List<ComponentTeam> teams,
            String platformId,
            List<ComponentConnection> connections,
            List<ComponentDependency> dependencies,
            List<CrossFunctionalRequirement> crossFunctionalRequirements,
            List<TechDebt> techDebts,
            List<Doc> docs,
            List<OpenApiSpec> openApiSpecs,
            List<GraphQlSchema> graphQlSchemas,
            List<ComponentState> states,
            List<ScannerError> scannerErrors,
            List<TestResult> testResults
    ) {
        this.id = id;
        this.aliases = createUnmodifiableList(aliases);
        this.name = name;
        this.discovered = discovered;
        this.type = type;
        this.tags = createUnmodifiableList(tags);
        this.repo = repo;
        this.description = description;
        this.responsibilities = createUnmodifiableList(responsibilities);
        this.notes = notes;
        this.links = links;
        this.teams = createUnmodifiableList(teams);
        this.platformId = platformId;
        this.connections = createUnmodifiableList(connections);
        this.dependencies = createUnmodifiableList(dependencies);
        this.crossFunctionalRequirements = createUnmodifiableList(crossFunctionalRequirements);
        this.techDebts = createUnmodifiableList(techDebts);
        this.docs = createUnmodifiableList(docs);
        this.openApiSpecs = createUnmodifiableList(openApiSpecs);
        this.graphQlSchemas = createUnmodifiableList(graphQlSchemas);
        this.states = createUnmodifiableList(states);
        this.scannerErrors = createUnmodifiableList(scannerErrors);
        this.testResults = createUnmodifiableList(testResults);
    }

    @Override
    public String reference() {
        return id;
    }

    public Component addTag(Tag tag) {
        return withTags(
                unmodifiableUnionOfLists(List.of(this.tags, List.of(tag)))
        );
    }

    public Component addTags(List<Tag> tags) {
        return withTags(
                unmodifiableUnionOfLists(List.of(this.tags, tags))
        );
    }

    public Component addState(ComponentState state) {
        return withStates(
                unmodifiableUnionOfLists(List.of(this.states, List.of(state)))
        );
    }

    public Component addStates(List<ComponentState> states) {
        return withStates(
                unmodifiableUnionOfLists(List.of(this.states, states))
        );
    }

    @JsonIgnore
    public <T extends ComponentState> List<T> getStates(String type) {
        return states.stream()
                .filter(state -> Objects.equals(state.getType(), type))
                .map(state -> (T) state)
                .collect(toUnmodifiableList());
    }

    @JsonIgnore
    public <T extends ComponentState> T getState(String type) {
        List<ComponentState> matches = getStates(type);
        if (matches.size() > 1) {
            throw new IllegalArgumentException("There are more than 1 states with type \"" + type + "\"");
        } else if (matches.isEmpty()) {
            return null;
        } else {
            return (T) matches.get(0);
        }
    }
}
