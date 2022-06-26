package tech.kronicle.service.graphql.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import tech.kronicle.service.graphql.models.ComponentsOutput;
import tech.kronicle.service.services.ComponentService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ComponentGraphQlController {

    private final ComponentService componentService;

    @QueryMapping
    public ComponentsOutput components() {
        return new ComponentsOutput(
                componentService.getComponents(Optional.empty(), Optional.empty(), List.of(), List.of(), List.of())
        );
    }
}
