package it.cyberqual.radiology_registry.application.equipment;

import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentRequest;
import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentResponse;
import it.cyberqual.radiology_registry.domain.model.*;
import it.cyberqual.radiology_registry.domain.validator.HierarchyValidator;
import it.cyberqual.radiology_registry.repository.NodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for creating radiology equipment.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentService {

    private final NodeRepository nodeRepository;
    private final HierarchyValidator hierarchyValidator;


    @Transactional
    public CreateEquipmentResponse create(CreateEquipmentRequest request) {
        log.info("Create equipment request received: name={}, type={}, parentId={}",
                request.name(), request.equipmentType(), request.parentId());

        Node parent = nodeRepository.findById(request.parentId())
                .orElseThrow(() -> {
                    log.warn("Parent not found for parentId={}", request.parentId());
                    return new IllegalArgumentException("Parent not found");
                });

        log.debug("Parent found: id={}, nodeType={}, rootOrganizationId={}",
                parent.getId(), parent.getNodeType(), parent.getRootOrganizationId());

        hierarchyValidator.validateParent(parent, NodeType.EQUIPMENT);
        log.debug("Hierarchy validation completed for parentId={}", parent.getId());

        Equipment equipment = new Equipment(
                UUID.randomUUID(),
                request.name(),
                parent,
                request.equipmentType(),
                request.serialNumber(),
                request.installationDate(),
                parent.getRootOrganizationId()
        );

        nodeRepository.save(equipment);
        log.info("Equipment created successfully: equipmentId={}, parentId={}, rootOrganizationId={}",
                equipment.getId(), parent.getId(), equipment.getRootOrganizationId());

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