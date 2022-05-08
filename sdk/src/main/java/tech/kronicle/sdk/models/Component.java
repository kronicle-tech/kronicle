package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.UniqueElements;
import tech.kronicle.graphql.codefirst.annotation.CodeFirstGraphQlIgnore;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.jackson.JsonRawValueDeserializer;
import tech.kronicle.sdk.jackson.JsonRawValueSerializer;
import tech.kronicle.sdk.jackson.TagOrStringDeserializer;
import tech.kronicle.sdk.models.git.GitRepo;
import tech.kronicle.sdk.models.gradle.Gradle;
import tech.kronicle.sdk.models.linesofcode.LinesOfCode;
import tech.kronicle.sdk.models.nodejs.NodeJs;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.sdk.models.readme.Readme;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.todos.ToDo;
import tech.kronicle.sdk.models.zipkin.Zipkin;
import tech.kronicle.sdk.utils.ListUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static java.util.Objects.nonNull;
import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;
import static tech.kronicle.sdk.utils.MapUtils.createUnmodifiableMap;

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
    @JsonAlias("type")
    String typeId;
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
    List<@Valid ComponentDependency> dependencies;
    List<@Valid CrossFunctionalRequirement> crossFunctionalRequirements;
    List<@Valid TechDebt> techDebts;
    @JsonSerialize(contentUsing = JsonRawValueSerializer.class)
    @JsonDeserialize(contentUsing = JsonRawValueDeserializer.class)
    @CodeFirstGraphQlIgnore
    Map<@NotEmpty String, String> plugins;

    @Valid
    ComponentState state;
    
    @Valid GitRepo gitRepo;
    @Valid Gradle gradle;
    @Valid NodeJs nodeJs;
    List<@Valid SoftwareRepository> softwareRepositories;
    List<@Valid Software> software;
    List<@Valid Import> imports;
    List<@Valid KeySoftware> keySoftware;
    @Valid LinesOfCode linesOfCode;
    @Valid List<ToDo> toDos;
    @Valid Readme readme;
    @Valid Zipkin zipkin;
    List<@Valid OpenApiSpec> openApiSpecs;
    List<@Valid SonarQubeProject> sonarQubeProjects;
    List<@Valid ScannerError> scannerErrors;
    List<@Valid TestResult> testResults;

    public Component(
            String id,
            List<Alias> aliases,
            String name,
            Boolean discovered,
            String typeId,
            List<Tag> tags,
            RepoReference repo,
            String description,
            List<Responsibility> responsibilities,
            String notes,
            List<Link> links,
            List<ComponentTeam> teams,
            String platformId,
            List<ComponentDependency> dependencies,
            List<CrossFunctionalRequirement> crossFunctionalRequirements,
            List<TechDebt> techDebts,
            Map<String, String> plugins,
            ComponentState state,
            GitRepo gitRepo,
            Gradle gradle,
            NodeJs nodeJs,
            List<SoftwareRepository> softwareRepositories,
            List<Software> software,
            List<Import> imports,
            List<KeySoftware> keySoftware,
            LinesOfCode linesOfCode,
            List<ToDo> toDos,
            Readme readme,
            Zipkin zipkin,
            List<OpenApiSpec> openApiSpecs,
            List<SonarQubeProject> sonarQubeProjects,
            List<ScannerError> scannerErrors,
            List<TestResult> testResults
    ) {
        this.id = id;
        this.aliases = createUnmodifiableList(aliases);
        this.name = name;
        this.discovered = discovered;
        this.typeId = typeId;
        this.tags = createUnmodifiableList(tags);
        this.repo = repo;
        this.description = description;
        this.responsibilities = createUnmodifiableList(responsibilities);
        this.notes = notes;
        this.links = links;
        this.teams = createUnmodifiableList(teams);
        this.platformId = platformId;
        this.dependencies = createUnmodifiableList(dependencies);
        this.crossFunctionalRequirements = createUnmodifiableList(crossFunctionalRequirements);
        this.techDebts = createUnmodifiableList(techDebts);
        this.plugins = createUnmodifiableMap(plugins);
        this.state = state;
        this.gitRepo = gitRepo;
        this.gradle = gradle;
        this.nodeJs = nodeJs;
        this.softwareRepositories = createUnmodifiableList(softwareRepositories);
        this.software = createUnmodifiableList(software);
        this.imports = createUnmodifiableList(imports);
        this.keySoftware = createUnmodifiableList(keySoftware);
        this.linesOfCode = linesOfCode;
        this.toDos = createUnmodifiableList(toDos);
        this.readme = readme;
        this.zipkin = zipkin;
        this.openApiSpecs = createUnmodifiableList(openApiSpecs);
        this.sonarQubeProjects = createUnmodifiableList(sonarQubeProjects);
        this.scannerErrors = createUnmodifiableList(scannerErrors);
        this.testResults = createUnmodifiableList(testResults);
    }

    @Override
    public String reference() {
        return id;
    }

    public Component withUpdatedState(UnaryOperator<ComponentState> action) {
        return withState(
                action.apply(nonNull(state) ? state : ComponentState.builder().build())
        );
    }

    public Component addImports(List<Import> imports) {
        return withImports(
                unmodifiableUnionOfLists(List.of(this.imports, imports))
        );
    }

    public Component addSoftwareRepositories(List<SoftwareRepository> softwareRepositories) {
        return withSoftwareRepositories(
                unmodifiableUnionOfLists(List.of(this.softwareRepositories, softwareRepositories))
        );
    }

    public Component addSoftware(List<Software> software) {
        return withSoftware(
                unmodifiableUnionOfLists(List.of(this.software, software))
        );
    }
}
