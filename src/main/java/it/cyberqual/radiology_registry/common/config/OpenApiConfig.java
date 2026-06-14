package it.cyberqual.radiology_registry.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for Radiology Registry.
 * Provides API documentation for the POC scope, focused on:
 * - Organizations
 * - Containers
 * - Medical Equipment
 */
@Configuration
public class OpenApiConfig {

    private static final String ROLE_HEADER_SCHEME = "RoleHeader";

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
                            """))
                // Declare the custom header security scheme
                .components(new Components()
                        .addSecuritySchemes(ROLE_HEADER_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-User-Role")
                                        .description("Simulated role header. Use 'ADMIN' to access write endpoints.")
                        )
                )
                // Apply globally — GET endpoints will silently ignore it
                .addSecurityItem(new SecurityRequirement().addList(ROLE_HEADER_SCHEME));
    }
}