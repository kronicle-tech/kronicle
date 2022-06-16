package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.sdk.models.GetComponentDiagramsResponse;
import tech.kronicle.sdk.models.GetDiagramsResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/diagrams")
public class DiagramController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Diagrams"},
            summary = "Get Diagrams",
            description = "Retrieves a list of all diagrams.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-diagrams"
    )
    @GetMapping
    @PartialResponse
    public GetDiagramsResponse getDiagrams() {
        return new GetDiagramsResponse(componentService.getDiagrams());
    }
}
