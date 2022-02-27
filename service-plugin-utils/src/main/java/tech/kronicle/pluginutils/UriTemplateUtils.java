package tech.kronicle.pluginutils;

import java.util.Map;

public final class UriTemplateUtils {

  public static String expandUriTemplate(String uriTemplate, Map<String, String> uriVariables) {
    return uriVariables.entrySet().stream().reduce(
            uriTemplate,
            (updatedUriTemplate, uriVariable) -> updatedUriTemplate.replace("{" + uriVariable.getKey() + "}", uriVariable.getValue()),
            (a, b) -> { throw new IllegalStateException("combiner function should not be used as this is not a parallel stream"); }
    );
  }

  private UriTemplateUtils() {
  }
}
