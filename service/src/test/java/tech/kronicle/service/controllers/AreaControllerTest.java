package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Area;
import tech.kronicle.sdk.models.GetAreaResponse;
import tech.kronicle.sdk.models.GetAreasResponse;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.service.services.ComponentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AreaControllerTest {

    private static final Area AREA_1 = Area.builder().id("test-area-1").build();
    private static final Area AREA_2 = Area.builder().id("test-area-2").build();
    private static final List<Area> AREAS = List.of(AREA_1, AREA_2);

    @Mock
    private ComponentService mockComponentService;
    private AreaController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new AreaController(mockComponentService);
    }

    @Test
    public void getAreasShouldReturnAreas() {
        // Given
        when(mockComponentService.getAreas(List.of())).thenReturn(AREAS);

        // When
        GetAreasResponse returnValue = underTest.getAreas(List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getAreas()).containsExactlyElementsOf(AREAS);
    }

    @Test
    public void getAreasShouldHandleNullFilter() {
        // Given
        when(mockComponentService.getAreas(List.of())).thenReturn(AREAS);

        // When
        GetAreasResponse returnValue = underTest.getAreas(null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getAreas()).containsExactlyElementsOf(AREAS);
    }

    @Test
    public void getAreasShouldPassFilterToAreaService() {
        // Given
        when(mockComponentService.getAreas(List.of(TestOutcome.FAIL))).thenReturn(AREAS);

        // When
        GetAreasResponse returnValue = underTest.getAreas(List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getAreas()).isSameAs(AREAS);
    }

    @Test
    public void getAreaShouldReturnAnArea() {
        // Given
        when(mockComponentService.getArea(AREA_1.getId(), List.of())).thenReturn(AREA_1);

        // When
        GetAreaResponse returnValue = underTest.getArea(AREA_1.getId(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getArea()).isSameAs(AREA_1);
    }

    @Test
    public void getAreaShouldNotReturnAnAreaWhenAreaIdIsUnknown() {
        // Given
        String areaId = "unknown";
        when(mockComponentService.getArea(areaId, List.of())).thenReturn(null);

        // When
        GetAreaResponse returnValue = underTest.getArea(areaId, List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getArea()).isNull();
    }

    @Test
    public void getAreaShouldHandleNullFilter() {
        // Given
        when(mockComponentService.getArea(AREA_1.getId(), List.of())).thenReturn(AREA_1);

        // When
        GetAreaResponse returnValue = underTest.getArea(AREA_1.getId(), null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getArea()).isEqualTo(AREA_1);
    }

    @Test
    public void getAreaShouldPassFilterToAreaService() {
        // Given
        when(mockComponentService.getArea(AREA_1.getId(), List.of(TestOutcome.FAIL))).thenReturn(AREA_1);

        // When
        GetAreaResponse returnValue = underTest.getArea(AREA_1.getId(), List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getArea()).isEqualTo(AREA_1);
    }
}
