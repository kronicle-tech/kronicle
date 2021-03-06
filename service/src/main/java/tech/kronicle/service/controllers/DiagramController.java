package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.kronicle.sdk.models.GetDiagramResponse;
import tech.kronicle.sdk.models.GetDiagramsResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

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
    public GetDiagramsResponse getDiagrams(
            @RequestParam(required = false) List<String> stateType,
            @RequestParam(required = false) List<String> stateId
    ) {
        return new GetDiagramsResponse(componentService.getDiagrams(
                createUnmodifiableList(stateType),
                createUnmodifiableList(stateId)
        ));
    }

    @Operation(
            tags = {"Diagrams"},
            summary = "Get Diagram",
            description = "Retrieves a diagram.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-diagram"
    )
    @GetMapping("/{diagramId}")
    @PartialResponse
    public GetDiagramResponse getDiagram(
            @PathVariable String diagramId,
            @RequestParam(required = false) List<String> stateType,
            @RequestParam(required = false) List<String> stateId
    ) {
        return new GetDiagramResponse(componentService.getDiagram(
                diagramId,
                createUnmodifiableList(stateType),
                createUnmodifiableList(stateId)
        ));
    }
}
