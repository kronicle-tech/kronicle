package tech.kronicle.plugins.gradle.internal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static tech.kronicle.common.StringEscapeUtils.escapeString;
import static tech.kronicle.pluginutils.StringUtils.requireNonEmpty;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class PropertyExpander {

    private final PropertyRetriever propertyRetriever;

    public String expandProperties(String value, String name, Map<String, String> properties, boolean bracesRequired) {
        requireNonEmpty(value, "value");
        requireNonEmpty(name, "name");
        requireNonNull(properties, "properties");
        int startIndex = value.indexOf("$");

        if (startIndex == -1) {
            return value;
        }

        StringBuilder newValue = new StringBuilder();
        int length = value.length();
        int endIndex = 0;

        do {
            newValue.append(value, endIndex, startIndex);

            if (startIndex + 1 == length) {
                throw new IllegalArgumentException(name + " with value \"" + escapeString(value)
                        + "\" contains an empty \"$\" property reference");
            }

            boolean hasBrace = value.charAt(startIndex + 1) == '{';
            boolean handled = false;

            if (hasBrace || !bracesRequired) {
                if (hasBrace) {
                    endIndex = value.indexOf("}", startIndex + 2);

                    if (endIndex == -1) {
                        throw new IllegalArgumentException(name + " with value \"" + escapeString(value)
                                + "\" contains an \"${\" property reference without a corresponding \"}\"");
                    }

                    endIndex++;
                } else {
                    endIndex = startIndex + 1;

                    if (!isPropertyNameCharacter(value.charAt(endIndex))) {
                        throw new IllegalArgumentException(name + " with value \"" + escapeString(value)
                                + "\" contains an \"$\" this is not followed by an alphanumeric character");
                    }

                    do {
                        endIndex++;
                    } while (endIndex < length && isPropertyNameCharacter(value.charAt(endIndex)));
                }

                String propertyName = value.substring(getPropertyNameStartIndex(startIndex, hasBrace), getPropertyNameEndIndex(endIndex, hasBrace));
                if (log.isDebugEnabled()) {
                    log.debug("Property name '{}'", escapeString(propertyName));
                }
                String propertyValue = propertyRetriever.getPropertyValue(propertyName, properties);
                if (log.isDebugEnabled()) {
                    log.debug("Property value '{}'", escapeString(propertyValue));
                }

                if (nonNull(propertyValue)) {
                    if (propertyValue.contains("$")) {
                        if (log.isDebugEnabled()) {
                            log.debug("Expanding properties in property value '{}'", escapeString(propertyValue));
                        }
                        propertyValue = expandProperties(propertyValue, name, properties, bracesRequired);
                    }

                    newValue.append(propertyValue);
                    handled = true;
                }
            } else {
                endIndex = startIndex + 1;
            }

            if (!handled) {
                newValue.append(value, startIndex, endIndex);
            }

            startIndex = value.indexOf("$", endIndex);
        } while (startIndex != -1);

        newValue.append(value, endIndex, length);
        return newValue.toString();
    }

    private int getPropertyNameStartIndex(int startIndex, boolean hasBrace) {
        return startIndex + (hasBrace ? 2 : 1);
    }

    private int getPropertyNameEndIndex(int endIndex, boolean hasBrace) {
        return endIndex + (hasBrace ? -1 : 0);
    }

    private boolean isPropertyNameCharacter(char character) {
        return (character >= 'a' && character <= 'z')
                || (character >= 'A' && character <= 'Z')
                || (character >= '0' && character <= '9')
                || character == '_'
                || character == '.';
    }
}
