package it.cyberqual.radiology_registry.application.equipment;

import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentRequest;
import it.cyberqual.radiology_registry.api.equipment.dto.EquipmentResponse;
import it.cyberqual.radiology_registry.domain.model.*;
import it.cyberqual.radiology_registry.repository.NodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for creating radiology equipment.
 */
@Service
@RequiredArgsConstructor
public class CreateEquipmentService {

    private final NodeRepository nodeRepository;

    @Transactional
    public EquipmentResponse create(CreateEquipmentRequest request) {

        Node parent = nodeRepository.findById(request.parentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));

        validateParent(parent);

        Equipment equipment = new Equipment(
                UUID.randomUUID(),
                request.name(),
                parent,
                request.equipmentType(),
                request.serialNumber(),
                request.installationDate()
        );

        nodeRepository.save(equipment);

        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                request.equipmentType(),
                request.serialNumber(),
                request.installationDate(),
                parent.getId()
        );
    }

    /**
     * Business rule:
     * Equipment can only be attached to Organization or Container.
     */
    private void validateParent(Node parent) {

        if (parent.getNodeType() == NodeType.EQUIPMENT) {
            throw new IllegalStateException("Equipment cannot be parent of another equipment");
        }

        if (parent.getNodeType() != NodeType.ORGANIZATION &&
                parent.getNodeType() != NodeType.CONTAINER) {
            throw new IllegalStateException("Invalid parent type for equipment");
        }
    }
}