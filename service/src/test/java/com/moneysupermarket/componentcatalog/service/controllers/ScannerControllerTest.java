package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetScannerResponse;
import com.moneysupermarket.componentcatalog.sdk.models.GetScannersResponse;
import com.moneysupermarket.componentcatalog.sdk.models.Scanner;
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
public class ScannerControllerTest {

    private static final Scanner SCANNER_1 = Scanner.builder().id("test-scanner-1").build();
    private static final Scanner SCANNER_2 = Scanner.builder().id("test-scanner-2").build();
    private static final List<Scanner> SCANNERS = List.of(SCANNER_1, SCANNER_2);
    
    @Mock
    private ComponentService mockComponentService;
    private ScannerController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ScannerController(mockComponentService);
    }

    @Test
    public void getScannersShouldReturnScanners() {
        // Given
        when(mockComponentService.getScanners()).thenReturn(SCANNERS);

        // When
        GetScannersResponse returnValue = underTest.getScanners();

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getScanners()).containsExactlyElementsOf(SCANNERS);
    }

    @Test
    public void getScannerShouldReturnAScanner() {
        // Given
        when(mockComponentService.getScanner(SCANNER_1.getId())).thenReturn(SCANNER_1);

        // When
        GetScannerResponse returnValue = underTest.getScanner(SCANNER_1.getId());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getScanner()).isSameAs(SCANNER_1);
    }

    @Test
    public void getScannerShouldNotReturnAScannerWhenScannerIdIsUnknown() {
        // Given
        String scannerId = "unknown";
        when(mockComponentService.getScanner(scannerId)).thenReturn(null);

        // When
        GetScannerResponse returnValue = underTest.getScanner(scannerId);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getScanner()).isNull();
    }
}
