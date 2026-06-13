package it.cyberqual.radiology_registry.domain.vo;

import jakarta.persistence.Embeddable;

import java.time.Instant;

/**
 * Audit information embedded in all nodes.
 */
@Embeddable
public class AuditInfo {

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}