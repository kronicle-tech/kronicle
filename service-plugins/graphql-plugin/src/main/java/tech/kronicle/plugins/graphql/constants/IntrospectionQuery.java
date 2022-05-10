package tech.kronicle.plugins.graphql.constants;

public final class IntrospectionQuery {

    public static final String INTROSPECTION_QUERY_JSON = "{\"query\":\"" +
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

    private IntrospectionQuery() {
    }
}
