package it.cyberqual.radiology_registry.repository;

import it.cyberqual.radiology_registry.domain.model.Container;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Container nodes.
 */
public interface ContainerRepository extends JpaRepository<Container, UUID> {

    /**
     * Find containers by parent node.
     */
    List<Container> findByParentId(UUID parentId);
}