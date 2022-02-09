package tech.kronicle.service.scanners.openapi.utils;

import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.service.scanners.openapi.models.SpecAndErrors;

import static java.util.Objects.isNull;

public final class OpenApiSpecUtils {

    public static boolean isManualSpec(SpecAndErrors specAndErrors) {
        return isManualSpec(specAndErrors.getSpec());
    }

    public static boolean isManualSpec(OpenApiSpec spec) {
        return isNull(spec.getScannerId());
    }
}
