package it.cyberqual.radiology_registry.domain.model;

import jakarta.persistence.Entity;

/**
 * Root entity representing a customer organization.
 *
 * Example:
 * - Hospital groups
 * - Local health authorities (ASL)
 */
@Entity
public class Organization extends Node {
}