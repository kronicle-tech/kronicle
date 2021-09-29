package tech.kronicle.service.springdoc;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {

    private static final String PARTIAL_RESPONSES_ARTICLE_URL =
            "https://dev.to/andersonjoseph/what-they-want-is-what-they-get-the-partial-response-strategy-5a0m";

    @Bean
    public OpenApiCustomiser openApiCustomiser() {
        return openApi -> openApi.getPaths().values().stream()
                .map(PathItem::getGet)
                .forEach(operation -> operation.addParametersItem(createFieldsQueryParameter()));
    }

    private Parameter createFieldsQueryParameter() {
        return new QueryParameter()
                .in("query")
                .name("fields")
                .description("Supports the Partial Responses feature that Google use on some of their APIs.  See " +
                        PARTIAL_RESPONSES_ARTICLE_URL + " for more information")
                .schema(new Schema<String>()
                        .type("string")
                        .minLength(1))
                .required(false);
    }

}
