package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.sdk.models.GetHealthResponse;
import tech.kronicle.sdk.models.GetHomeResponse;
import tech.kronicle.service.partialresponse.PartialResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {

    @Operation(
            tags = {"Home"},
            summary = "Get Health",
            description = "Returns a 200 status code.  ",
            operationId = "get-health"
    )
    @GetMapping
    @PartialResponse
    public GetHealthResponse getHealth() {
        return new GetHealthResponse();
    }
}
