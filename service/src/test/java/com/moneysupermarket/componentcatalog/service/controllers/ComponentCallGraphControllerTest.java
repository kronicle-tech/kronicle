package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetComponentCallGraphsResponse;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryCallGraph;
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
public class ComponentCallGraphControllerTest {

    @Mock
    private ComponentService mockComponentService;
    private ComponentCallGraphController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ComponentCallGraphController(mockComponentService);
    }

    @Test
    public void getComponentCallGraphsShouldReturnComponentCallGraphs() {
        // Given
        String componentId = "test-component-id-1";
        SummaryCallGraph callGraph1 = SummaryCallGraph.builder().traceCount(1).build();
        SummaryCallGraph callGraph2 = SummaryCallGraph.builder().traceCount(2).build();
        when(mockComponentService.getComponentCallGraphs(componentId)).thenReturn(List.of(callGraph1, callGraph2));

        // When
        GetComponentCallGraphsResponse returnValue = underTest.getComponentCallGraphs(componentId);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getCallGraphs()).containsExactly(callGraph1, callGraph2);
    }

    @Test
    public void getComponentCallGraphsShouldNotReturnComponentCallGraphsWhenComponentIdIsUnknown() {
        // Given
        String componentId = "unknown";
        when(mockComponentService.getComponentCallGraphs(componentId)).thenReturn(List.of());

        // When
        GetComponentCallGraphsResponse returnValue = underTest.getComponentCallGraphs(componentId);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getCallGraphs()).isEmpty();
    }
}
