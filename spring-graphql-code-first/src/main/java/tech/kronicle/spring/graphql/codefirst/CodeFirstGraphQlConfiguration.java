package tech.kronicle.spring.graphql.codefirst;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.kronicle.graphql.codefirst.CodeFirstGraphQlSchemaGenerator;
import tech.kronicle.graphql.codefirst.CodeFirstInputTypeMapper;
import tech.kronicle.graphql.codefirst.CodeFirstOutputTypeMapper;

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
            CodeFirstInputTypeMapper inputTypeMapper,
            CodeFirstOutputTypeMapper outputTypeMapper
    ) {
        return new CodeFirstGraphQlSchemaGenerator(inputTypeMapper, outputTypeMapper);
    }

    @Bean
    public CodeFirstGraphQlSchemaResourceGenerator codeFirstGraphQlSchemaResourceGenerator(
            ListableBeanFactory beanFactory,
            CodeFirstGraphQlSchemaGenerator codeFirstGraphQlSchemaGenerator
    ) {
        return new CodeFirstGraphQlSchemaResourceGenerator(beanFactory, codeFirstGraphQlSchemaGenerator);
    }

    @Bean
    public GraphQlSourceBuilderCustomizer codeFirstGraphQlSourceBuilderCustomizer(
            CodeFirstGraphQlSchemaResourceGenerator codeFirstGraphQlSchemaResourceGenerator
    ) {
        return new CodeFirstGraphQlSourceBuilderCustomizer(codeFirstGraphQlSchemaResourceGenerator);
    }
}
