package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.Team;
import com.moneysupermarket.componentcatalog.sdk.models.GetTeamResponse;
import com.moneysupermarket.componentcatalog.sdk.models.GetTeamsResponse;
import com.moneysupermarket.componentcatalog.sdk.models.TestOutcome;
import com.moneysupermarket.componentcatalog.service.services.ComponentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamControllerTest {

    private static final Team TEAM_1 = Team.builder().id("test-team-1").build();
    private static final Team TEAM_2 = Team.builder().id("test-team-2").build();
    private static final List<Team> TEAMS = List.of(TEAM_1, TEAM_2);

    @Mock
    private ComponentService mockComponentService;
    private TeamController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new TeamController(mockComponentService);
    }

    @Test
    public void getTeamsShouldReturnTeams() {
        // Given
        when(mockComponentService.getTeams(List.of())).thenReturn(TEAMS);

        // When
        GetTeamsResponse returnValue = underTest.getTeams(List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeams()).containsExactlyElementsOf(TEAMS);
    }

    @Test
    public void getTeamsShouldHandleNullFilter() {
        // Given
        when(mockComponentService.getTeams(List.of())).thenReturn(TEAMS);

        // When
        GetTeamsResponse returnValue = underTest.getTeams(null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeams()).containsExactlyElementsOf(TEAMS);
    }

    @Test
    public void getTeamsShouldPassFilterToTeamService() {
        // Given
        when(mockComponentService.getTeams(List.of(TestOutcome.FAIL))).thenReturn(TEAMS);

        // When
        GetTeamsResponse returnValue = underTest.getTeams(List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeams()).isSameAs(TEAMS);
    }

    @Test
    public void getTeamShouldReturnATeam() {
        // Given
        when(mockComponentService.getTeam(TEAM_1.getId(), List.of())).thenReturn(TEAM_1);

        // When
        GetTeamResponse returnValue = underTest.getTeam(TEAM_1.getId(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeam()).isSameAs(TEAM_1);
    }

    @Test
    public void getTeamShouldNotReturnATeamWhenTeamIdIsUnknown() {
        // Given
        String teamId = "unknown";
        when(mockComponentService.getTeam(teamId, List.of())).thenReturn(null);

        // When
        GetTeamResponse returnValue = underTest.getTeam(teamId, List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeam()).isNull();
    }

    @Test
    public void getTeamShouldHandleNullFilter() {
        // Given
        when(mockComponentService.getTeam(TEAM_1.getId(), List.of())).thenReturn(TEAM_1);

        // When
        GetTeamResponse returnValue = underTest.getTeam(TEAM_1.getId(), null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeam()).isEqualTo(TEAM_1);
    }

    @Test
    public void getTeamShouldPassFilterToTeamService() {
        // Given
        when(mockComponentService.getTeam(TEAM_1.getId(), List.of(TestOutcome.FAIL))).thenReturn(TEAM_1);

        // When
        GetTeamResponse returnValue = underTest.getTeam(TEAM_1.getId(), List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeam()).isEqualTo(TEAM_1);
    }
}
