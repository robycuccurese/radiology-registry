package it.cyberqual.radiology_registry.application.organization;

import it.cyberqual.radiology_registry.api.equipment.dto.EquipmentLightDto;
import it.cyberqual.radiology_registry.api.organization.dto.NodeTreeDto;
import it.cyberqual.radiology_registry.repository.NodeRepository;
import it.cyberqual.radiology_registry.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OrganizationService {

    private final NodeRepository nodeRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public NodeTreeDto getTree(UUID organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new NoSuchElementException("Organization not found: " + organizationId);
        }

        List<NodeRepository.NodeTreeRow> rows = nodeRepository.findFullTreeRowsByRootOrganizationId(organizationId);

        // Legacy safeguard: if old rows still miss root_org_id, fallback to recursive traversal from root.
        if (rows.size() <= 1) {
            rows = nodeRepository.findFullTreeRowsRecursive(organizationId);
        }

        Map<UUID, NodeTreeDto> dtoMap = new HashMap<>();

        for (NodeRepository.NodeTreeRow row : rows) {

            NodeTreeDto dto = new NodeTreeDto(row.getId(), row.getName(), row.getNodeType(), new ArrayList<>(), null);

            if (row.getSerialNumber() != null) {
                dto.setEquipmentDetails(new EquipmentLightDto(row.getSerialNumber(), row.getInstallationDate()));
            }

            dtoMap.put(row.getId(), dto);
        }

        for (NodeRepository.NodeTreeRow row : rows) {
            if (row.getParentId() == null) {
                continue;
            }

            NodeTreeDto current = dtoMap.get(row.getId());
            NodeTreeDto parent = dtoMap.get(row.getParentId());

            if (parent != null) {
                parent.getChildren().add(current);
            }
        }

        NodeTreeDto root = dtoMap.get(organizationId);
        if (root == null) {
            throw new IllegalStateException("Tree root not found for organization: " + organizationId);
        }
        return root;
    }
}