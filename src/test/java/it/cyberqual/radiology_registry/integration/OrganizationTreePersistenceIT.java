package it.cyberqual.radiology_registry.integration;

import it.cyberqual.radiology_registry.api.organization.dto.NodeTreeDto;
import it.cyberqual.radiology_registry.application.organization.OrganizationService;
import it.cyberqual.radiology_registry.domain.model.Container;
import it.cyberqual.radiology_registry.domain.model.Equipment;
import it.cyberqual.radiology_registry.domain.model.EquipmentType;
import it.cyberqual.radiology_registry.domain.model.Organization;
import it.cyberqual.radiology_registry.repository.NodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test that verifies real persistence and retrieval against PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrganizationTreePersistenceIT {

    private final NodeRepository nodeRepository;
    private final OrganizationService organizationService;

    OrganizationTreePersistenceIT(NodeRepository nodeRepository, OrganizationService organizationService) {
        this.nodeRepository = nodeRepository;
        this.organizationService = organizationService;
    }

    @Test
    void shouldPersistAndReadOrganizationTreeFromDatabase() {
        UUID orgId = UUID.randomUUID();
        Organization organization = new Organization(orgId, "ASL Integration");

        Container container = new Container(
                UUID.randomUUID(),
                "Radiology Department",
                organization,
                orgId
        );

        Equipment equipment = new Equipment(
                UUID.randomUUID(),
                "GE Revolution CT",
                container,
                EquipmentType.CT,
                "INT-" + UUID.randomUUID(),
                LocalDate.of(2024, 1, 15),
                orgId
        );

        nodeRepository.saveAll(List.of(organization, container, equipment));

        List<NodeRepository.NodeTreeRow> rows = nodeRepository.findFullTreeRowsByRootOrganizationId(orgId);
        assertEquals(3, rows.size());

        NodeTreeDto tree = organizationService.getTree(orgId);

        assertEquals(orgId, tree.getId());
        assertEquals("ASL Integration", tree.getName());
        assertEquals(1, tree.getChildren().size());

        NodeTreeDto containerNode = tree.getChildren().getFirst();
        assertEquals("Radiology Department", containerNode.getName());
        assertEquals(1, containerNode.getChildren().size());

        NodeTreeDto equipmentNode = containerNode.getChildren().getFirst();
        assertEquals("GE Revolution CT", equipmentNode.getName());
        assertNotNull(equipmentNode.getEquipmentDetails());
    }
}



