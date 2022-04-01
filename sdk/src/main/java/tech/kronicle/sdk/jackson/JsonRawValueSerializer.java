package tech.kronicle.sdk.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class JsonRawValueSerializer extends StdSerializer<String> {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    protected JsonRawValueSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeTree(JSON_MAPPER.readTree(value));
    }
}
