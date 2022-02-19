package tech.kronicle.service.utils;

import org.springframework.web.util.UriTemplate;

import java.util.Map;

public final class UriTemplateUtils {

  public static String expandUriTemplate(String uriTemplate, Map<String, String> uriVariables) {
    return new UriTemplate(uriTemplate).expand(uriVariables).toString();
  }

  private UriTemplateUtils() {
  }
}
