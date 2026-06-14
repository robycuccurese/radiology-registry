package it.cyberqual.radiology_registry.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the Radiology Registry API.
 *
 * <p>Access rules:
 * <ul>
 *   <li>GET requests are publicly accessible (no role required).</li>
 *   <li>POST requests require the {@code ADMIN} role, supplied via the
 *       {@code X-User-Role: ADMIN} HTTP header.</li>
 *   <li>Swagger UI and OpenAPI docs are always accessible.</li>
 * </ul>
 *
 * <p>Authentication is simulated: no login endpoint or token is required.
 * The {@link RoleHeaderAuthenticationFilter} reads the {@code X-User-Role} header
 * and injects the corresponding authority into the {@code SecurityContext}.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Stateless API — no session, no CSRF needed
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)

                // Inject role from header before the standard filter
                .addFilterBefore(
                        new RoleHeaderAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )

                .authorizeHttpRequests(auth -> auth
                        // Always allow Swagger UI and OpenAPI spec
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // All GET requests are public
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()

                        // POST requests require ADMIN role
                        .requestMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")

                        // Everything else denied by default
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}

