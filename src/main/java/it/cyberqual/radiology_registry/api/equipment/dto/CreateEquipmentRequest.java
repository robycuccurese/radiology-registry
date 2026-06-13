package it.cyberqual.radiology_registry.api.equipment.dto;

import it.cyberqual.radiology_registry.domain.model.EquipmentType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request used to create a new radiology equipment.
 */
public record CreateEquipmentRequest(
        String name,
        EquipmentType equipmentType,
        String serialNumber,
        LocalDate installationDate,
        UUID parentId
) {}