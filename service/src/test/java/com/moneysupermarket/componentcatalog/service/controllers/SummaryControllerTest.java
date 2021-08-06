package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetSummaryResponse;
import com.moneysupermarket.componentcatalog.sdk.models.Summary;
import com.moneysupermarket.componentcatalog.service.services.ComponentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SummaryControllerTest {

    @Mock
    private ComponentService mockComponentService;
    private SummaryController underTest;
    public static final Summary TEST_SUMMARY = Summary.EMPTY;

    @BeforeEach
    public void beforeEach() {
        underTest = new SummaryController(mockComponentService);
    }

    @Test
    public void getComponentShouldReturnTheSummary() {
        // Given
        when(mockComponentService.getSummary()).thenReturn(TEST_SUMMARY);

        // When
        GetSummaryResponse returnValue = underTest.getSummary();

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getSummary()).isSameAs(TEST_SUMMARY);
    }
}
