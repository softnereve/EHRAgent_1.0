package com.softnerve.epic.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
@OpenAPIDefinition(
        info = @Info(
                title = "Patient Microservice API",
                version = "1.0",
                description = "APIs for Patient Microservice API Practice Platform"),
        servers = {

                @Server(
                        url = "http://localhost:7979/v1.0.0/apis/fhir",
                        description = "Local server"
                ),
        }
)
@Configuration
public class SwaggerConfiguration {
    /**
     * Configures the Swagger Docket bean.
     * This bean is used to generate API documentation for the controllers in the specified package.
     *
     * @return a Docket instance configured for Swagger 2 documentation.
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.softnerve.epic.controller"))
                .build();
    }
}
