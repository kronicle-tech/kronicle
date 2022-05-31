package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.GetComponentResponse;
import tech.kronicle.sdk.models.GetComponentsResponse;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.service.services.ComponentService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComponentControllerTest {

    private static final Component COMPONENT_1 = Component.builder().id("test-component-1").build();
    private static final Component COMPONENT_2 = Component.builder().id("test-component-2").build();
    private static final List<Component> COMPONENTS = List.of(COMPONENT_1, COMPONENT_2);
    
    @Mock
    private ComponentService mockComponentService;
    private ComponentController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ComponentController(mockComponentService);
    }

    @Test
    public void getComponentsShouldReturnComponents() {
        // Given
        when(mockComponentService.getComponents(Optional.empty(), Optional.empty(), List.of())).thenReturn(COMPONENTS);

        // When
        GetComponentsResponse returnValue = underTest.getComponents(Optional.empty(), Optional.empty(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponents()).containsExactlyElementsOf(COMPONENTS);
    }

    @Test
    public void getComponentsShouldHandleNullFilters() {
        // Given
        when(mockComponentService.getComponents(null, null, List.of())).thenReturn(COMPONENTS);

        // When
        GetComponentsResponse returnValue = underTest.getComponents(null, null, null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponents()).containsExactlyElementsOf(COMPONENTS);
    }

    @Test
    public void getComponentsShouldPassAllFiltersToComponentService() {
        // Given
        when(mockComponentService.getComponents(Optional.of(1), Optional.of(2), List.of(TestOutcome.FAIL))).thenReturn(COMPONENTS);

        // When
        GetComponentsResponse returnValue = underTest.getComponents(Optional.of(1), Optional.of(2), List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponents()).isSameAs(COMPONENTS);
    }

    @Test
    public void getComponentShouldReturnAComponent() {
        // Given
        when(mockComponentService.getComponent(COMPONENT_1.getId(), List.of())).thenReturn(COMPONENT_1);

        // When
        GetComponentResponse returnValue = underTest.getComponent(COMPONENT_1.getId(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponent()).isSameAs(COMPONENT_1);
    }

    @Test
    public void getComponentShouldNotReturnAComponentWhenComponentIdIsUnknown() {
        // Given
        String componentId = "unknown";
        when(mockComponentService.getComponent(componentId, List.of())).thenReturn(null);

        // When
        GetComponentResponse returnValue = underTest.getComponent(componentId, List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponent()).isNull();
    }

    @Test
    public void getComponentShouldFilterStateTypes() {
        // Given
        String componentId = "unknown";
        List<String> stateTypes = List.of("test-state-type-1", "test-state-type-2");
        when(mockComponentService.getComponent(componentId, stateTypes)).thenReturn(COMPONENT_1);

        // When
        GetComponentResponse returnValue = underTest.getComponent(componentId, stateTypes);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponent()).isEqualTo(COMPONENT_1);
    }
}
