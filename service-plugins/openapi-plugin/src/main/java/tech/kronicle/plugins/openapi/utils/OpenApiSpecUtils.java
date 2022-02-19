package tech.kronicle.plugins.openapi.utils;

import tech.kronicle.plugins.openapi.models.SpecAndErrors;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;

import static java.util.Objects.isNull;

public final class OpenApiSpecUtils {

    public static boolean isManualSpec(SpecAndErrors specAndErrors) {
        return isManualSpec(specAndErrors.getSpec());
    }

    public static boolean isManualSpec(OpenApiSpec spec) {
        return isNull(spec.getScannerId());
    }
}
