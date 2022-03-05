package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.utils.EnumUtils;
import tech.kronicle.sdk.models.GetAreaResponse;
import tech.kronicle.sdk.models.GetAreasResponse;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

import java.util.List;

import static tech.kronicle.utils.EnumUtils.getEnumListFromJsonValues;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/areas")
public class AreaController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Areas"},
            summary = "Get Areas",
            description = "Retrieves a list of all areas.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-areas"
    )
    @GetMapping
    @PartialResponse
    public GetAreasResponse getAreas(@RequestParam(required = false) List<String> testOutcome) {
        return new GetAreasResponse(componentService.getAreas(getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }

    @Operation(
            tags = {"Areas"},
            summary = "Get Area",
            description = "Retrieves an area.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-area"
    )
    @GetMapping("/{areaId}")
    @PartialResponse
    public GetAreaResponse getArea(@PathVariable String areaId, @RequestParam(required = false) List<String> testOutcome) {
        return new GetAreaResponse(componentService.getArea(areaId, getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }
}
