package it.cyberqual.radiology_registry.api.equipment.dto;

import it.cyberqual.radiology_registry.domain.model.EquipmentType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload representing the persisted equipment record.
 */
public record CreateEquipmentResponse(
        UUID id,
        String name,
        EquipmentType equipmentType,
        String serialNumber,
        LocalDate installationDate,
        UUID parentId
) {}