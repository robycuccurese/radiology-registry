package it.cyberqual.radiology_registry.application.equipment;

import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentRequest;
import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentResponse;
import it.cyberqual.radiology_registry.domain.model.*;
import it.cyberqual.radiology_registry.domain.validator.HierarchyValidator;
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
    private final HierarchyValidator hierarchyValidator;


    @Transactional
    public CreateEquipmentResponse create(CreateEquipmentRequest request) {

        Node parent = nodeRepository.findById(request.parentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));

        hierarchyValidator.validateParent(parent, NodeType.EQUIPMENT);

        Equipment equipment = new Equipment(
                UUID.randomUUID(),
                request.name(),
                parent,
                request.equipmentType(),
                request.serialNumber(),
                request.installationDate()
        );

        nodeRepository.save(equipment);

        return new CreateEquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                request.equipmentType(),
                request.serialNumber(),
                request.installationDate(),
                parent.getId()
        );
    }
}