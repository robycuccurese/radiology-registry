package it.cyberqual.radiology_registry.repository;

import it.cyberqual.radiology_registry.domain.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Equipment nodes.
 */
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    /**
     * Find all equipment attached to a specific parent node
     * (Organization or Container).
     */
    List<Equipment> findByParentId(UUID parentId);
}