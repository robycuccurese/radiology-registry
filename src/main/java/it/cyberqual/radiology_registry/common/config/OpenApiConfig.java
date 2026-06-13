package it.cyberqual.radiology_registry.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for Radiology Registry.
 *
 * Provides API documentation for hierarchical management of:
 * - Organizations
 * - Containers
 * - Medical Equipment
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Radiology Registry API")
                        .version("1.0.0")
                        .description("""
                            REST API for managing a hierarchical radiology asset registry.
                            
                            The system models a recursive structure composed of:
                            - Organization (root entity)
                            - Container (nested structural units)
                            - Equipment (medical devices)
                            
                            Features:
                            - Equipment creation under Organization or Container
                            - Full hierarchical tree retrieval
                            """));
    }
}