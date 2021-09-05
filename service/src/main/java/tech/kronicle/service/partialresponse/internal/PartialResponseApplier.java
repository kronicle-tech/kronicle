package tech.kronicle.service.partialresponse.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.common.collect.Lists;
import com.pressassociation.pr.match.Matcher;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
public class PartialResponseApplier {

    public void apply(JsonNode json, Matcher matcher) {
        apply(json, matcher, "");
    }

    private void apply(JsonNode json, Matcher matcher, String path) {
        if (json instanceof ObjectNode) {
            apply((ObjectNode) json, matcher, path);
        } else if (json instanceof ArrayNode) {
            apply((ArrayNode) json, matcher, path);
        }
    }

    private void apply(ObjectNode json, Matcher matcher, String path) {
        List<String> fieldNames = Lists.newArrayList(json.fieldNames());

        for (String fieldName : fieldNames) {
            String childPath = joinPath(path, fieldName);

            JsonNode fieldValue = json.get(fieldName);

            if (fieldValue instanceof ValueNode) {
                if (!matches(matcher, childPath)) {
                    json.remove(fieldName);
                }
            } else {
                if (!matches(matcher, childPath)) {
                    json.remove(fieldName);
                } else {
                    apply(fieldValue, matcher, childPath);
                }
            }
        }
    }

    private void apply(ArrayNode json, Matcher matcher, String path) {
        for (int i = 0, count = json.size(); i < count; i++) {
            apply(json.get(i), matcher, path);
        }
    }

    private String joinPath(String path1, String path2) {
        if (isNull(path1) || path1.isEmpty()) {
            return path2;
        } else {
            return path1 + "/" + path2;
        }
    }

    private boolean matches(Matcher matcher, String path) {
        return matcher.matches(path) || matcher.matchesParent(path);
    }
}
