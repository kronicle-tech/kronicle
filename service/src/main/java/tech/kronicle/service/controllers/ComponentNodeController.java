package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.sdk.models.GetComponentNodesResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components/{componentId}/nodes")
public class ComponentNodeController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Component Nodes"},
            summary = "Get Nodes for a Component",
            description = "Retrieves a list of all nodes for a component.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-nodes-for-component"
    )
    @GetMapping
    @PartialResponse
    public GetComponentNodesResponse getComponentNodes(@PathVariable String componentId) {
        return new GetComponentNodesResponse(componentService.getComponentNodes(componentId));
    }
}
