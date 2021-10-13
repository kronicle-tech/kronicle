package tech.kronicle.service.springdoc.spring;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.kronicle.service.springdoc.config.OpenApiSpecConfig;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Configuration
public class SpringdocConfig {

    private static final String PARTIAL_RESPONSES_ARTICLE_URL =
            "https://dev.to/andersonjoseph/what-they-want-is-what-they-get-the-partial-response-strategy-5a0m";

    @Bean
    public OpenApiCustomiser openApiCustomiser(OpenApiSpecConfig config, @Value("${info.app.version}") String version) {
        return openApi -> {
            openApi.info(new Info()
                    .title("Kronicle Service")
                    .description("The Kronicle Service contains all the data loaded into Kronicle via `kronicle.yaml` " +
                            "files and data pulled in from external sources via Kronicle Scanners.  All this data can " +
                            "be retrieved via the service's endpoints.  \n" +
                            "\n" +
                            "The service's endpoints support a `fields` query parameter that can be used to limit the " +
                            "JSON objects, arrays and fields that are included in JSON response bodies.  This is similar " +
                            "to GraphQL's ability to specify what fields to return in a response.  See " +
                            PARTIAL_RESPONSES_ARTICLE_URL + " for more information.  ")
                    .version(version));

            if (nonNull(config.getClearExistingServers()) && config.getClearExistingServers()) {
                openApi.servers(null);
            }

            if (nonNull(config.getServers())) {
                config.getServers().forEach(server -> openApi.addServersItem(new Server()
                        .url(server.getUrl())
                        .description(server.getDescription())));
            }

            openApi.getPaths().values().stream()
                    .map(PathItem::getGet)
                    .filter(Objects::nonNull)
                    .forEach(operation -> operation.addParametersItem(createFieldsQueryParameter()));
        };
    }

    private Parameter createFieldsQueryParameter() {
        return new QueryParameter()
                .name("fields")
                .description("Supports the Partial Responses feature that Google uses on some of their APIs.  See " +
                        PARTIAL_RESPONSES_ARTICLE_URL + " for more information")
                .schema(new Schema<String>()
                        .type("string")
                        .minLength(1))
                .required(false);
    }

}
