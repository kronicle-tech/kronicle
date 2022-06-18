package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.*;
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
        when(mockComponentService.getComponents(Optional.empty(), Optional.empty(), List.of(), List.of())).thenReturn(COMPONENTS);

        // When
        GetComponentsResponse returnValue = underTest.getComponents(Optional.empty(), Optional.empty(), List.of(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponents()).containsExactlyElementsOf(COMPONENTS);
    }

    @Test
    public void getComponentsShouldHandleNullFilters() {
        // Given
        when(mockComponentService.getComponents(null, null, List.of(), List.of())).thenReturn(COMPONENTS);

        // When
        GetComponentsResponse returnValue = underTest.getComponents(null, null, null, null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponents()).containsExactlyElementsOf(COMPONENTS);
    }

    @Test
    public void getComponentsShouldPassFiltersToComponentService() {
        // Given
        when(mockComponentService.getComponents(Optional.of(1), Optional.of(2), List.of("test-state-type-1"), List.of(TestOutcome.FAIL))).thenReturn(COMPONENTS);

        // When
        GetComponentsResponse returnValue = underTest.getComponents(Optional.of(1), Optional.of(2), List.of("test-state-type-1"), List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponents()).isSameAs(COMPONENTS);
    }

    @Test
    public void getComponentShouldReturnAComponent() {
        // Given
        when(mockComponentService.getComponent(COMPONENT_1.getId(), List.of(), List.of())).thenReturn(COMPONENT_1);

        // When
        GetComponentResponse returnValue = underTest.getComponent(COMPONENT_1.getId(), List.of(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponent()).isSameAs(COMPONENT_1);
    }

    @Test
    public void getComponentShouldNotReturnAComponentWhenComponentIdIsUnknown() {
        // Given
        String componentId = "unknown";
        when(mockComponentService.getComponent(componentId, List.of(), List.of())).thenReturn(null);

        // When
        GetComponentResponse returnValue = underTest.getComponent(componentId, List.of(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponent()).isNull();
    }

    @Test
    public void getComponentShouldHandleNullFilters() {
        // Given
        when(mockComponentService.getComponent(null, List.of(), List.of())).thenReturn(COMPONENT_1);

        // When
        GetComponentResponse returnValue = underTest.getComponent(null, null, null);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponent()).isSameAs(COMPONENT_1);
    }

    @Test
    public void getComponentShouldPassFiltersToComponentService() {
        // Given
        when(mockComponentService.getComponent(COMPONENT_1.getId(), List.of("test-state-type-1"), List.of(TestOutcome.FAIL))).thenReturn(COMPONENT_1);

        // When
        GetComponentResponse returnValue = underTest.getComponent(COMPONENT_1.getId(), List.of("test-state-type-1"), List.of(TestOutcome.FAIL.value()));

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getComponent()).isSameAs(COMPONENT_1);
    }
}
