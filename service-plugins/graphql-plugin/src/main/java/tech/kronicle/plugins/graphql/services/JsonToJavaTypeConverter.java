package tech.kronicle.plugins.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.utils.MapCollectors.toUnmodifiableMap;

public final class JsonToJavaTypeConverter {

    public static Map<String, Object> toMap(ObjectNode node) {
        return (Map<String, Object>) convert(node);
    }

    public static List<Object> toList(ArrayNode node) {
        return (List<Object>) convert(node);
    }

    public static Object convert(JsonNode node) {
        if (node.isObject()) {
            return toStream(node.fields())
                    .map(entry -> Map.entry(entry.getKey(), convert(entry.getValue())))
                    .collect(toUnmodifiableMap());
        } else if (node.isArray()) {
            return toStream(node.elements())
                    .map(JsonToJavaTypeConverter::convert)
                    .collect(toUnmodifiableList());
        } else if (node.isShort()) {
            return node.shortValue();
        } else if (node.isInt()) {
            return node.intValue();
        } else if (node.isLong()) {
            return node.longValue();
        } else if (node.isFloat()) {
            return node.floatValue();
        } else if (node.isDouble()) {
            return node.doubleValue();
        } else if (node.isBigDecimal()) {
            return node.numberValue();
        } else if (node.isBigInteger()) {
            return node.bigIntegerValue();
        } else if (node.isTextual()) {
            return node.textValue();
        } else if (node.isBoolean()) {
            return node.booleanValue();
        } else if (node.isNull()) {
            return null;
        } else {
            throw new IllegalStateException("Unexpected node type " + node.getNodeType());
        }
    }
    
    private static <T> Stream<T> toStream(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), 
                false
        );
    }

    private JsonToJavaTypeConverter() {
    }
}
