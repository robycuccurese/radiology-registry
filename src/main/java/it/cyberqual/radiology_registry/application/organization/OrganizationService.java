package it.cyberqual.radiology_registry.application.organization;

import it.cyberqual.radiology_registry.api.equipment.dto.EquipmentLightDto;
import it.cyberqual.radiology_registry.api.organization.dto.NodeTreeDto;
import it.cyberqual.radiology_registry.repository.NodeRepository;
import it.cyberqual.radiology_registry.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Builds organization tree using a flat query + in-memory reconstruction.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationService {

    private final NodeRepository nodeRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public NodeTreeDto getTree(UUID organizationId) {
        log.info("Organization tree request received: organizationId={}", organizationId);
        if (!organizationRepository.existsById(organizationId)) {
            log.warn("Organization not found: organizationId={}", organizationId);
            throw new NoSuchElementException("Organization not found: " + organizationId);
        }

        List<NodeRepository.NodeTreeRow> rows = nodeRepository.findFullTreeRowsByRootOrganizationId(organizationId);
        log.debug("Indexed query completed: organizationId={}, rowsCount={}", organizationId, rows.size());

        // Legacy safeguard: if old rows still miss root_org_id, fallback to recursive traversal from root.
        if (rows.size() <= 1) {
            log.info("Recursive query fallback activated: organizationId={}, initialRowsCount={}", organizationId, rows.size());
            rows = nodeRepository.findFullTreeRowsRecursive(organizationId);
            log.debug("Recursive fallback completed: organizationId={}, rowsCount={}", organizationId, rows.size());
        }

        Map<UUID, NodeTreeDto> dtoMap = new HashMap<>();

        for (NodeRepository.NodeTreeRow row : rows) {

            NodeTreeDto dto = new NodeTreeDto(row.getId(), row.getName(), row.getNodeType(), new ArrayList<>(), null);

            if (row.getSerialNumber() != null) {
                dto.setEquipmentDetails(new EquipmentLightDto(row.getSerialNumber(), row.getInstallationDate()));
            }

            dtoMap.put(row.getId(), dto);
        }
        log.debug("DTO node map built: organizationId={}, nodeCount={}", organizationId, dtoMap.size());

        for (NodeRepository.NodeTreeRow row : rows) {
            if (row.getParentId() == null) {
                continue;
            }

            NodeTreeDto current = dtoMap.get(row.getId());
            NodeTreeDto parent = dtoMap.get(row.getParentId());

            if (parent != null) {
                parent.getChildren().add(current);
            } else {
                log.warn("Missing parent during node linking: currentId={}, expectedParentId={}", row.getId(), row.getParentId());
            }
        }

        NodeTreeDto root = dtoMap.get(organizationId);
        if (root == null) {
            log.error("Tree root not found after reconstruction: organizationId={}, availableNodeCount={}", organizationId, dtoMap.size());
            throw new IllegalStateException("Tree root not found for organization: " + organizationId);
        }
        log.info("Organization tree built successfully: organizationId={}, rootChildrenCount={}",
                organizationId, root.getChildren().size());
        return root;
    }
}