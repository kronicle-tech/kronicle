package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.kronicle.sdk.models.GetComponentResponse;
import tech.kronicle.sdk.models.GetComponentsResponse;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

import java.util.List;
import java.util.Optional;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;
import static tech.kronicle.utils.EnumUtils.getEnumListFromJsonValues;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components")
public class ComponentController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Components"},
            summary = "Get Components",
            description = "Retrieves a list of all components.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-components"
    )
    @GetMapping
    @PartialResponse
    public GetComponentsResponse getComponents(
            @RequestParam(required = false) Optional<Integer> offset,
            @RequestParam(required = false) Optional<Integer> limit,
            @RequestParam(required = false) List<String> stateType,
            @RequestParam(required = false) List<String> testOutcome
    ) {
        return new GetComponentsResponse(componentService.getComponents(
                offset,
                limit,
                createUnmodifiableList(stateType),
                getEnumListFromJsonValues(TestOutcome.class, testOutcome)
        ));
    }

    @Operation(
            tags = {"Components"},
            summary = "Get Component",
            description = "Retrieves a component.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-component"
    )
    @GetMapping("/{componentId}")
    @PartialResponse
    public GetComponentResponse getComponent(
            @PathVariable String componentId,
            @RequestParam(required = false) List<String> stateType,
            @RequestParam(required = false) List<String> testOutcome
    ) {
        return new GetComponentResponse(componentService.getComponent(
                componentId,
                createUnmodifiableList(stateType),
                getEnumListFromJsonValues(TestOutcome.class, testOutcome)
        ));
    }
}
