package it.cyberqual.radiology_registry.repository;

import it.cyberqual.radiology_registry.domain.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for Organization root entities.
 */
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
}