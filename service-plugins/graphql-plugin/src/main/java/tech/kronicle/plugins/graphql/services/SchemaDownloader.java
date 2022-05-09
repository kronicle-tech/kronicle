package tech.kronicle.plugins.graphql.services;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import tech.kronicle.plugins.graphql.config.GraphQlConfig;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SchemaDownloader {

    private static final String GRAPHQL_INTROSPECTION_QUERY = "{\"query\":\"" + 
            "query IntrospectionQuery {\\n" +
            "  __schema {\\n" +
            "    queryType {\\n" +
            "      name\\n" +
            "    }\\n" +
            "    mutationType {\\n" +
            "      name\\n" +
            "    }\\n" +
            "    subscriptionType {\\n" +
            "      name\\n" +
            "    }\\n" +
            "    types {\\n" +
            "      ...FullType\\n" +
            "    }\\n" +
            "    directives {\\n" +
            "      name\\n" +
            "      description\\n" +
            "      locations\\n" +
            "      args {\\n" +
            "        ...InputValue\\n" +
            "      }\\n" +
            "    }\\n" +
            "  }\\n" +
            "}\\n" +
            "\\n" +
            "fragment FullType on __Type {\\n" +
            "  kind\\n" +
            "  name\\n" +
            "  description\\n" +
            "  fields(includeDeprecated: true) {\\n" +
            "    name\\n" +
            "    description\\n" +
            "    args {\\n" +
            "      ...InputValue\\n" +
            "    }\\n" +
            "    type {\\n" +
            "      ...TypeRef\\n" +
            "    }\\n" +
            "    isDeprecated\\n" +
            "    deprecationReason\\n" +
            "  }\\n" +
            "  inputFields {\\n" +
            "    ...InputValue\\n" +
            "  }\\n" +
            "  interfaces {\\n" +
            "    ...TypeRef\\n" +
            "  }\\n" +
            "  enumValues(includeDeprecated: true) {\\n" +
            "    name\\n" +
            "    description\\n" +
            "    isDeprecated\\n" +
            "    deprecationReason\\n" +
            "  }\\n" +
            "  possibleTypes {\\n" +
            "    ...TypeRef\\n" +
            "  }\\n" +
            "}\\n" +
            "\\n" +
            "fragment InputValue on __InputValue {\\n" +
            "  name\\n" +
            "  description\\n" +
            "  type {\\n" +
            "    ...TypeRef\\n" +
            "  }\\n" +
            "  defaultValue\\n" +
            "}\\n" +
            "\\n" +
            "fragment TypeRef on __Type {\\n" +
            "  kind\\n" +
            "  name\\n" +
            "  ofType {\\n" +
            "    kind\\n" +
            "    name\\n" +
            "    ofType {\\n" +
            "      kind\\n" +
            "      name\\n" +
            "      ofType {\\n" +
            "        kind\\n" +
            "        name\\n" +
            "        ofType {\\n" +
            "          kind\\n" +
            "          name\\n" +
            "          ofType {\\n" +
            "            kind\\n" +
            "            name\\n" +
            "            ofType {\\n" +
            "              kind\\n" +
            "              name\\n" +
            "              ofType {\\n" +
            "                kind\\n" +
            "                name\\n" +
            "              }\\n" +
            "            }\\n" +
            "          }\\n" +
            "        }\\n" +
            "      }\\n" +
            "    }\\n" +
            "  }\\n" +
            "}\\n" + 
            "\"}";

    private final GraphQlConfig config;
    private final HttpClient httpClient;

    @SneakyThrows
    public String downloadSchema(String url) {
        HttpResponse<String> response = httpClient.send(
                createHttpRequestBuilder(config.getTimeout())
                        .POST(HttpRequest.BodyPublishers.ofString(GRAPHQL_INTROSPECTION_QUERY))
                        .uri(URI.create(url))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        return response.body();
    }
}
