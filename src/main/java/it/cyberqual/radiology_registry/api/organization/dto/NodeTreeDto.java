package it.cyberqual.radiology_registry.api.organization.dto;

import it.cyberqual.radiology_registry.api.equipment.dto.EquipmentLightDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tree DTO representing Organization / Container / Equipment hierarchy.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeTreeDto {

    private UUID id;
    private String name;
    private String type;

    private List<NodeTreeDto> children = new ArrayList<>();

    private EquipmentLightDto equipmentDetails;
}