package tech.kronicle.pluginutils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class JsonMapperFactory {

    public static JsonMapper createJsonMapper() {
        return (JsonMapper) new JsonMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonMapperFactory() {
    }
}
