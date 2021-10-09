package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import tech.kronicle.sdk.models.GetSummaryResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.service.springdoc.Texts;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/summary")
public class SummaryController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Scanners"},
            summary = "Get Summary",
            description = "Retrieves the summary.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-summary"
    )
    @GetMapping
    @PartialResponse
    public GetSummaryResponse getSummary() {
        return new GetSummaryResponse(componentService.getSummary());
    }
}
