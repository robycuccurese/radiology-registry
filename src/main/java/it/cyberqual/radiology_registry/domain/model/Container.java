package it.cyberqual.radiology_registry.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Hierarchical structural node.
 * <p>
 * Containers can be nested without limits.
 * <p>
 * Examples:
 * - Building
 * - Department
 * - Ward
 * - Room
 */
@Entity
@Table(name = "container")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Container extends Node {
    public Container(UUID id, String name, Node parent, UUID rootOrganizationId) {
        super(id, name, NodeType.CONTAINER, parent, rootOrganizationId);
    }
}