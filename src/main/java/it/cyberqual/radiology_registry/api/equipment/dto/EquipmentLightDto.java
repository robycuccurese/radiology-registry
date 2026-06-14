package it.cyberqual.radiology_registry.api.equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Leaf DTO for medical equipment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentLightDto {
    private String serialNumber;
    private LocalDate installationDate;
}