package tech.kronicle.service.controllers;

import tech.kronicle.sdk.models.GetTeamResponse;
import tech.kronicle.sdk.models.GetTeamsResponse;
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
@RequestMapping("/v1/teams")
public class TeamController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetTeamsResponse getTeams(@RequestParam(required = false) List<String> testOutcome) {
        return new GetTeamsResponse(componentService.getTeams(EnumUtils.getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }

    @GetMapping("/{teamId}")
    @PartialResponse
    public GetTeamResponse getTeam(@PathVariable String teamId, @RequestParam(required = false) List<String> testOutcome) {
        return new GetTeamResponse(componentService.getTeam(teamId, EnumUtils.getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }
}
