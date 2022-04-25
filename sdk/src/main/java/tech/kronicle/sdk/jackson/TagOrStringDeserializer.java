package tech.kronicle.sdk.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import tech.kronicle.sdk.models.Tag;

import java.io.IOException;

public class TagOrStringDeserializer extends StdDeserializer<Tag> {

    protected TagOrStringDeserializer() {
        super(Tag.class);
    }

    @Override
    public Tag deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            return Tag.builder()
                    .key(p.getValueAsString())
                    .build();
        } else {
            return p.readValueAs(Tag.class);
        }
    }
}
