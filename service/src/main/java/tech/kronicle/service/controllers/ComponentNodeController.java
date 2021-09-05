package tech.kronicle.service.controllers;

import tech.kronicle.sdk.models.GetComponentNodesResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components/{componentId}")
public class ComponentNodeController {

    private final ComponentService componentService;

    @GetMapping("/nodes")
    @PartialResponse
    public GetComponentNodesResponse getComponentNodes(@PathVariable String componentId) {
        return new GetComponentNodesResponse(componentService.getComponentNodes(componentId));
    }
}
