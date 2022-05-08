package tech.kronicle.spring.graphql.codefirst;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodeFirstGraphQlConfiguration {

    @Bean
    public CodeFirstInputTypeMapper codeFirstInputTypeMapper() {
        return new CodeFirstInputTypeMapper();
    }

    @Bean
    public CodeFirstOutputTypeMapper codeFirstOutputTypeMapper() {
        return new CodeFirstOutputTypeMapper();
    }

    @Bean
    public CodeFirstGraphQlSchemaGenerator codeFirstGraphQlSchemaGenerator(
            ListableBeanFactory beanFactory,
            CodeFirstInputTypeMapper inputTypeMapper,
            CodeFirstOutputTypeMapper outputTypeMapper
    ) {
        return new CodeFirstGraphQlSchemaGenerator(beanFactory, inputTypeMapper, outputTypeMapper);
    }

    @Bean
    public GraphQlSourceBuilderCustomizer codeFirstGraphQlSourceBuilderCustomizer(CodeFirstGraphQlSchemaGenerator graphQlSchemaGenerator) {
        return new CodeFirstGraphQlSourceBuilderCustomizer(graphQlSchemaGenerator);
    }
}
