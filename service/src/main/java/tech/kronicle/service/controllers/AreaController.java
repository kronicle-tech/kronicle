package tech.kronicle.service.controllers;

import tech.kronicle.sdk.models.GetAreaResponse;
import tech.kronicle.sdk.models.GetAreasResponse;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.service.utils.EnumUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/areas")
public class AreaController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetAreasResponse getAreas(@RequestParam(required = false) List<String> testOutcome) {
        return new GetAreasResponse(componentService.getAreas(EnumUtils.getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }

    @GetMapping("/{areaId}")
    @PartialResponse
    public GetAreaResponse getArea(@PathVariable String areaId, @RequestParam(required = false) List<String> testOutcome) {
        return new GetAreaResponse(componentService.getArea(areaId, EnumUtils.getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }
}
