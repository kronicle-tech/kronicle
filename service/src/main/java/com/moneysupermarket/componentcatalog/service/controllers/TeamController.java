package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetTeamResponse;
import com.moneysupermarket.componentcatalog.sdk.models.GetTeamsResponse;
import com.moneysupermarket.componentcatalog.sdk.models.TestOutcome;
import com.moneysupermarket.componentcatalog.service.partialresponse.PartialResponse;
import com.moneysupermarket.componentcatalog.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.moneysupermarket.componentcatalog.service.utils.EnumUtils.getEnumListFromJsonValues;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/teams")
public class TeamController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetTeamsResponse getTeams(@RequestParam(required = false) List<String> testOutcome) {
        return new GetTeamsResponse(componentService.getTeams(getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }

    @GetMapping("/{teamId}")
    @PartialResponse
    public GetTeamResponse getTeam(@PathVariable String teamId, @RequestParam(required = false) List<String> testOutcome) {
        return new GetTeamResponse(componentService.getTeam(teamId, getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }
}
