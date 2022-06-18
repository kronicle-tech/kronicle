package tech.kronicle.service.graphql.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import tech.kronicle.service.graphql.models.AreasOutput;
import tech.kronicle.service.services.ComponentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AreaGraphQlController {

    private final ComponentService componentService;

    @QueryMapping
    public AreasOutput areas() {
        return new AreasOutput(
                componentService.getAreas(List.of(), List.of())
        );
    }
}
