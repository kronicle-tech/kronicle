package tech.kronicle.service.graphql.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import tech.kronicle.service.graphql.models.TeamsOutput;
import tech.kronicle.service.services.ComponentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeamGraphQlController {

    private final ComponentService componentService;

    @QueryMapping
    public TeamsOutput teams() {
        return new TeamsOutput(
                componentService.getTeams(List.of(), List.of(), List.of())
        );
    }
}
