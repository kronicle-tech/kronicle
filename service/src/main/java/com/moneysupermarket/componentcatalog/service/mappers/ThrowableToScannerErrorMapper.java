package com.moneysupermarket.componentcatalog.service.mappers;

import com.moneysupermarket.componentcatalog.sdk.models.ScannerError;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
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
