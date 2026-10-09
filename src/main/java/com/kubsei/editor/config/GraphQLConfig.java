package com.kubsei.editor.config;

import graphql.language.StringValue;
import graphql.scalars.ExtendedScalars;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.Json)
                .scalar(instantDateTimeScalar());
    }

    /**
     * Custom DateTime scalar that handles both Instant and OffsetDateTime.
     * This allows Spring Data MongoDB's @CreatedDate/@LastModifiedDate (which use Instant)
     * to work with GraphQL's DateTime type.
     */
    private GraphQLScalarType instantDateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("A date-time string at UTC with ISO-8601 format")
                .coercing(new Coercing<Instant, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof Instant instant) {
                            return instant.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        } else if (dataFetcherResult instanceof OffsetDateTime odt) {
                            return odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        }
                        throw new CoercingSerializeException(
                                "Expected Instant or OffsetDateTime but was " + dataFetcherResult.getClass().getName());
                    }

                    @Override
                    public Instant parseValue(Object input) throws CoercingParseValueException {
                        try {
                            if (input instanceof String str) {
                                return OffsetDateTime.parse(str, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
                            }
                            throw new CoercingParseValueException("Expected a String");
                        } catch (Exception e) {
                            throw new CoercingParseValueException("Invalid DateTime format: " + e.getMessage());
                        }
                    }

                    @Override
                    public Instant parseLiteral(Object input) throws CoercingParseLiteralException {
                        if (input instanceof StringValue stringValue) {
                            try {
                                return OffsetDateTime.parse(stringValue.getValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
                            } catch (Exception e) {
                                throw new CoercingParseLiteralException("Invalid DateTime format: " + e.getMessage());
                            }
                        }
                        throw new CoercingParseLiteralException("Expected a StringValue");
                    }
                })
                .build();
    }
}
