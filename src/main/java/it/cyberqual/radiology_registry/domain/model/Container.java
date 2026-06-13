package it.cyberqual.radiology_registry.domain.model;

import jakarta.persistence.Entity;

/**
 * Structural node used to represent physical or logical subdivisions.
 *
 * Containers can be nested without depth limitation.
 *
 * Examples:
 * - Buildings
 * - Departments
 * - Rooms
 */
@Entity
public class Container extends Node {
}