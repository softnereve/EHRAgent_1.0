package com.softnerve.epic.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "EHRAgent Epic API",
                version = "1.0",
                description = "Standalone service for Epic FHIR Integration"),
        servers = {
                @Server(
                        url = "http://localhost:8282/v1.0.0/apis/epic",
                        description = "Local server"
                ),
                @Server(
                        url = "https://kong.softnerve.com/v1.0.0/apis/epic",
                        description = "Prod Server"
                )
        }
)
@Configuration
public class SwaggerConfiguration {
    // Basic Swagger config using SpringDoc
}
