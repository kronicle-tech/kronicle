package tech.kronicle.utils;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.ScannerError;

import static org.assertj.core.api.Assertions.assertThat;

public class ThrowableToScannerErrorMapperTest {

    private static final String SCANNER_ID = "test-scanner-id";
    private final ThrowableToScannerErrorMapper underTest = new ThrowableToScannerErrorMapper();

    @Test
    public void mapShouldTransformAnExceptionWithNoCauseToAScannerError() {
        // Given
        RuntimeException exception = new RuntimeException("Test message");

        // When
        ScannerError returnValue = underTest.map(SCANNER_ID, exception);

        // Then
        assertThat(returnValue).isEqualTo(ScannerError.builder()
                .scannerId(SCANNER_ID)
                .message("Test message")
                .build());
    }
}
