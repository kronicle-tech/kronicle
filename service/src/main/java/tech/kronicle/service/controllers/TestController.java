package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.sdk.models.GetTestResponse;
import tech.kronicle.sdk.models.GetTestsResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tests")
public class TestController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Tests"},
            summary = "Get Tests",
            description = "Retrieves a list of all tests.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-tests"
    )
    @GetMapping
    @PartialResponse
    public GetTestsResponse getTests() {
        return new GetTestsResponse(componentService.getTests());
    }

    @Operation(
            tags = {"Tests"},
            summary = "Get Test",
            description = "Retrieves a test.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-test"
    )
    @GetMapping("/{testId}")
    @PartialResponse
    public GetTestResponse getTest(@PathVariable String testId) {
        return new GetTestResponse(componentService.getTest(testId));
    }
}
