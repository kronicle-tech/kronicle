package com.moneysupermarket.componentcatalog.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScannerErrorTest {

    @Test
    public void toStringWhenCauseIsNullShouldReturnMessage() {
        // Given
        ScannerError underTest = new ScannerError("test_scanner1", "test_message1", null);

        // When
        String returnValue = underTest.toString();

        // Then
        assertThat(returnValue).isEqualTo("test_message1");
    }

    @Test
    public void toStringWhenCauseIs1DeepShouldReturnMessagePlus1CauseMessage() {
        // Given
        ScannerError underTest = new ScannerError("test_scanner1", "test_message1",
                new ScannerError("test_scanner1", "test_message2", null));

        // When
        String returnValue = underTest.toString();

        // Then
        assertThat(returnValue).isEqualTo("test_message1 | test_message2");
    }

    @Test
    public void toStringWhenCauseIs2DeepShouldReturnMessagePlus2CauseMessages() {
        // Given
        ScannerError underTest = new ScannerError("test_scanner1", "test_message1",
                new ScannerError("test_scanner1", "test_message2",
                        new ScannerError("test_scanner1", "test_message3", null)));

        // When
        String returnValue = underTest.toString();

        // Then
        assertThat(returnValue).isEqualTo("test_message1 | test_message2 | test_message3");
    }
}
