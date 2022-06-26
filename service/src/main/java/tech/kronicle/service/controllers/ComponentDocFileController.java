package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.kronicle.sdk.models.GetComponentDocFileResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import tech.kronicle.service.springdoc.Texts;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components/{componentId}/docs/{docId}")
public class ComponentDocFileController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Component Doc Files"},
            summary = "Get Component Doc File",
            description = "Retrieves a component doc file.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-component-doc-file"
    )
    @GetMapping("/files/file")
    @PartialResponse
    public GetComponentDocFileResponse getComponentDocFile(
            @PathVariable String componentId,
            @PathVariable String docId,
            @RequestParam String docFilePath
    ) {
        return new GetComponentDocFileResponse(componentService.getComponentDocFile(componentId, docId, docFilePath));
    }
}
