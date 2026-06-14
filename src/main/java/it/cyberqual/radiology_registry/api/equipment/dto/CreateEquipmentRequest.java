package it.cyberqual.radiology_registry.api.equipment.dto;

import it.cyberqual.radiology_registry.domain.model.EquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload used to create a new radiology equipment record.
 */
public record CreateEquipmentRequest(
        @NotBlank String name,
        @NotNull EquipmentType equipmentType,
        @NotBlank String serialNumber,
        @NotNull LocalDate installationDate,
        @NotNull UUID parentId
) {}