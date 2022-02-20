package tech.kronicle.service.scanners.services;

import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import static java.util.Objects.nonNull;

@SpringComponent
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
