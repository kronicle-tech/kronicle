package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.sdk.models.GetComponentDiagramsResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components/{componentId}/diagrams")
public class ComponentDiagramController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Component Diagrams"},
            summary = "Get Diagrams for a Component",
            description = "Retrieves a list of all diagrams that include the component.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-diagrams-for-component"
    )
    @GetMapping
    @PartialResponse
    public GetComponentDiagramsResponse getComponentDiagrams(@PathVariable String componentId) {
        return new GetComponentDiagramsResponse(componentService.getComponentDiagrams(componentId));
    }
}
