package it.cyberqual.radiology_registry.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

/**
 * Leaf entity representing a radiological medical device.
 *
 * Equipment can only be attached to:
 * - Organization
 * - Container
 *
 * It cannot contain child nodes.
 */
@Entity
public class Equipment extends Node {

    private String serialNumber;
    private LocalDate installationDate;

    @Enumerated(EnumType.STRING)
    private EquipmentType equipmentType;

    public String getSerialNumber() { return serialNumber; }
    public LocalDate getInstallationDate() { return installationDate; }
    public EquipmentType getEquipmentType() { return equipmentType; }

    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }
}