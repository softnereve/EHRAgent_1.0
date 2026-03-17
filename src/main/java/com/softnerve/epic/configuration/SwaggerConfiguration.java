package com.softnerve.epic.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
@OpenAPIDefinition(
        info = @Info(
                title = "Patient Microservice API",
                version = "1.0",
                description = "APIs for Patient Microservice API Practice Platform"),
        servers = {

                @Server(
                        url = "http://localhost:8282/v1.0.0/apis/patient",
                        description = "Local server"
                ),
                @Server(
                        url = "https://kong.softnerve.com/v1.0.0/apis/patient",
                        description = "Prod Server"
                ),
                @Server(
                        url = "https://softnerve.com/v1.0.0/apis/patient",
                        description = "staging Server"
                ),
        }
)
@Configuration
public class SwaggerConfiguration {
}
