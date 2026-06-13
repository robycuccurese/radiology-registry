package it.cyberqual.radiology_registry.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Medical radiology equipment.
 * <p>
 * Equipment is always a leaf node.
 * It can be attached either to:
 * - Organization
 * - Container
 */
@Getter
@Entity
@Table(name = "equipment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Equipment extends Node {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentType equipmentType;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Column(nullable = false)
    private LocalDate installationDate;

    public Equipment(UUID id, String name, Node parent, EquipmentType equipmentType, String serialNumber, LocalDate installationDate) {
        super(id, name, NodeType.EQUIPMENT, parent);

        this.equipmentType = equipmentType;
        this.serialNumber = serialNumber;
        this.installationDate = installationDate;
    }
}