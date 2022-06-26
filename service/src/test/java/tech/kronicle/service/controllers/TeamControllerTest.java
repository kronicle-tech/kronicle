package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.*;
import tech.kronicle.service.services.ComponentService;

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
        when(mockComponentService.getTeams(List.of(), List.of(), List.of())).thenReturn(TEAMS);

        // When
        GetTeamsResponse returnValue = underTest.getTeams(List.of(), List.of(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeams()).containsExactlyElementsOf(TEAMS);
    }

    @Test
    public void getTeamsShouldHandleNullFilters() {
        // Given
        when(mockComponentService.getTeams(List.of(), List.of(), List.of())).thenReturn(TEAMS);

        // When
        GetTeamsResponse returnValue = underTest.getTeams(null, null, null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeams()).containsExactlyElementsOf(TEAMS);
    }

    @Test
    public void getTeamsShouldPassFiltersToComponentService() {
        // Given
        when(mockComponentService.getTeams(List.of("test-state-type-1"), List.of("test-state-id-1"), List.of(TestOutcome.FAIL))).thenReturn(TEAMS);

        // When
        GetTeamsResponse returnValue = underTest.getTeams(List.of("test-state-type-1"), List.of("test-state-id-1"), List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeams()).isSameAs(TEAMS);
    }

    @Test
    public void getTeamShouldReturnATeam() {
        // Given
        when(mockComponentService.getTeam(TEAM_1.getId(), List.of(), List.of(), List.of())).thenReturn(TEAM_1);

        // When
        GetTeamResponse returnValue = underTest.getTeam(TEAM_1.getId(), List.of(), List.of(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeam()).isSameAs(TEAM_1);
    }

    @Test
    public void getTeamShouldNotReturnATeamWhenTeamIdIsUnknown() {
        // Given
        String teamId = "unknown";
        when(mockComponentService.getTeam(teamId, List.of(), List.of(), List.of())).thenReturn(null);

        // When
        GetTeamResponse returnValue = underTest.getTeam(teamId, List.of(), List.of(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeam()).isNull();
    }

    @Test
    public void getTeamShouldHandleNullFilters() {
        // Given
        when(mockComponentService.getTeam(TEAM_1.getId(), List.of(), List.of(), List.of())).thenReturn(TEAM_1);

        // When
        GetTeamResponse returnValue = underTest.getTeam(TEAM_1.getId(), null, null, null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeam()).isEqualTo(TEAM_1);
    }

    @Test
    public void getTeamShouldPassFiltersToComponentService() {
        // Given
        when(mockComponentService.getTeam(TEAM_1.getId(), List.of("test-state-type-1"), List.of("test-state-id-1"), List.of(TestOutcome.FAIL))).thenReturn(TEAM_1);

        // When
        GetTeamResponse returnValue = underTest.getTeam(TEAM_1.getId(), List.of("test-state-type-1"), List.of("test-state-id-1"), List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTeam()).isSameAs(TEAM_1);
    }
}
