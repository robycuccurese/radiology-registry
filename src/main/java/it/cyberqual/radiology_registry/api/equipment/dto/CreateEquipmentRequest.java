package it.cyberqual.radiology_registry.api.equipment.dto;

import it.cyberqual.radiology_registry.domain.model.EquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request used to create a new radiology equipment.
 */
public record CreateEquipmentRequest(
        @NotNull @NotBlank String name,
        @NotNull @NotBlank EquipmentType equipmentType,
        @NotNull @NotBlank String serialNumber,
        @NotNull @NotBlank LocalDate installationDate,
        @NotNull @NotBlank UUID parentId
) {}