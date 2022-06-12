package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.sdk.models.GetComponentCallGraphsResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components/{componentId}/call-graphs")
public class ComponentCallGraphController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Component Call Graphs"},
            summary = "Get Call Graphs for a Component",
            description = "Retrieves a list of all call graphs for a component.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-call-graphs-for-component"
    )
    @GetMapping
    @PartialResponse
    public GetComponentCallGraphsResponse getComponentCallGraphs(@PathVariable String componentId) {
        return new GetComponentCallGraphsResponse(componentService.getComponentCallGraphs(componentId));
    }
}
