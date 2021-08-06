package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetComponentNodesResponse;
import com.moneysupermarket.componentcatalog.sdk.models.SummarySubComponentDependencyNode;
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
public class ComponentNodeControllerTest {

    @Mock
    private ComponentService mockComponentService;
    private ComponentNodeController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ComponentNodeController(mockComponentService);
    }

    @Test
    public void getComponentNodesShouldReturnComponentNodes() {
        // Given
        String componentId = "test-component-id-1";
        SummarySubComponentDependencyNode node1 = SummarySubComponentDependencyNode.builder().spanName("test-span-name-1").build();
        SummarySubComponentDependencyNode node2 = SummarySubComponentDependencyNode.builder().spanName("test-span-name-2").build();
        when(mockComponentService.getComponentNodes(componentId)).thenReturn(List.of(node1, node2));

        // When
        GetComponentNodesResponse returnValue = underTest.getComponentNodes(componentId);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getNodes()).containsExactly(node1, node2);
    }

    @Test
    public void getComponentNodesShouldNotReturnComponentNodesWhenComponentIdIsUnknown() {
        // Given
        String componentId = "unknown";
        when(mockComponentService.getComponentNodes(componentId)).thenReturn(List.of());

        // When
        GetComponentNodesResponse returnValue = underTest.getComponentNodes(componentId);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getNodes()).isEmpty();
    }
}
