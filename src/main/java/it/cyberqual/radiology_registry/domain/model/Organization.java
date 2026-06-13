package it.cyberqual.radiology_registry.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Root node representing a customer organization.
 * <p>
 * Examples:
 * - Hospital Group
 * - Health Authority
 * - Medical Network
 */
@Entity
@Table(name = "organization")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization extends Node {

    public Organization(UUID id, String name) {
        super(id, name, NodeType.ORGANIZATION, null);
    }
}