package tech.kronicle.utils;

import tech.kronicle.sdk.models.ScannerError;

import static java.util.Objects.nonNull;

public class ThrowableToScannerErrorMapper {

    public ScannerError map(String scannerId, Throwable throwable) {
        return map(scannerId, "", throwable);
    }

    public ScannerError map(String scannerId, String messagePrefix, Throwable throwable) {
        Throwable causeThrowable = throwable.getCause();
        ScannerError cause = nonNull(causeThrowable) ? map(scannerId, causeThrowable) : null;
        return new ScannerError(scannerId, messagePrefix + throwable.getMessage(), cause);
    }
}
