package it.cyberqual.radiology_registry.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data auditing support.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}