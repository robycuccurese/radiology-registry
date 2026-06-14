package it.cyberqual.radiology_registry.application.organization;

import it.cyberqual.radiology_registry.api.organization.dto.NodeTreeDto;
import it.cyberqual.radiology_registry.repository.NodeRepository;
import it.cyberqual.radiology_registry.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(OrganizationServiceTest.class);

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        log.debug("Test setup: initializing OrganizationService with repository mocks");
        organizationService = new OrganizationService(nodeRepository, organizationRepository);
    }

    @Test
    void shouldBuildFullTreeWithNestedContainersAndEquipment() {
        log.info("Test started: building full tree with nested containers and equipment");
        UUID orgId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID containerA = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID containerB = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID equipment1 = UUID.fromString("44444444-4444-4444-4444-444444444444");
        UUID equipment2 = UUID.fromString("55555555-5555-5555-5555-555555555555");

        when(organizationRepository.existsById(orgId)).thenReturn(true);
        when(nodeRepository.findFullTreeRowsByRootOrganizationId(orgId)).thenReturn(List.of(
                row(orgId, "ASL 1", "ORGANIZATION", null, null, null),
                row(containerA, "Building A", "CONTAINER", orgId, null, null),
                row(containerB, "Radiology", "CONTAINER", containerA, null, null),
                row(equipment1, "GE Revolution CT", "EQUIPMENT", containerB, "GE-REV-001", LocalDate.of(2024, 1, 15)),
                row(equipment2, "Mobile CT", "EQUIPMENT", orgId, "MOBILE-CT-001", LocalDate.of(2024, 3, 20))
        ));

        NodeTreeDto tree = organizationService.getTree(orgId);
        log.debug("Tree obtained, validating structure and equipment details");

        assertEquals(orgId, tree.getId());
        assertEquals(2, tree.getChildren().size());

        NodeTreeDto buildingA = tree.getChildren().stream()
                .filter(c -> c.getId().equals(containerA))
                .findFirst()
                .orElseThrow();

        assertEquals(1, buildingA.getChildren().size());
        assertEquals(containerB, buildingA.getChildren().getFirst().getId());

        NodeTreeDto nestedEquipment = buildingA.getChildren().getFirst().getChildren().getFirst();
        assertEquals(equipment1, nestedEquipment.getId());
        assertNotNull(nestedEquipment.getEquipmentDetails());
        assertEquals("GE-REV-001", nestedEquipment.getEquipmentDetails().getSerialNumber());
        log.info("Test completed successfully: full tree reconstructed correctly");
    }

    @Test
    void shouldFallbackToRecursiveQueryWhenRootIndexQueryReturnsOnlyRoot() {
        log.info("Test started: fallback to recursive query when indexed query returns only root");
        UUID orgId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID equipmentId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        when(organizationRepository.existsById(orgId)).thenReturn(true);
        when(nodeRepository.findFullTreeRowsByRootOrganizationId(orgId)).thenReturn(List.of(
                row(orgId, "ASL Legacy", "ORGANIZATION", null, null, null)
        ));
        when(nodeRepository.findFullTreeRowsRecursive(orgId)).thenReturn(List.of(
                row(orgId, "ASL Legacy", "ORGANIZATION", null, null, null),
                row(equipmentId, "Legacy Mobile CT", "EQUIPMENT", orgId, "LEG-001", LocalDate.of(2023, 6, 1))
        ));

        NodeTreeDto tree = organizationService.getTree(orgId);
        log.debug("Fallback executed, verifying expected child node presence");

        assertEquals(1, tree.getChildren().size());
        assertEquals(equipmentId, tree.getChildren().getFirst().getId());
        log.info("Test completed successfully: recursive fallback working");
    }

    @Test
    void shouldThrowWhenOrganizationDoesNotExist() {
        log.info("Test started: exception expected when organization does not exist");
        UUID orgId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

        when(organizationRepository.existsById(orgId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> organizationService.getTree(orgId));
        log.info("Test completed successfully: NoSuchElementException thrown as expected");
    }

    private NodeRepository.NodeTreeRow row(
            UUID id,
            String name,
            String nodeType,
            UUID parentId,
            String serialNumber,
            LocalDate installationDate
    ) {
        return new NodeTreeRowStub(id, name, nodeType, parentId, serialNumber, installationDate);
    }

    private static final class NodeTreeRowStub implements NodeRepository.NodeTreeRow {
        private final UUID id;
        private final String name;
        private final String nodeType;
        private final UUID parentId;
        private final String serialNumber;
        private final LocalDate installationDate;

        private NodeTreeRowStub(UUID id, String name, String nodeType, UUID parentId, String serialNumber, LocalDate installationDate) {
            this.id = id;
            this.name = name;
            this.nodeType = nodeType;
            this.parentId = parentId;
            this.serialNumber = serialNumber;
            this.installationDate = installationDate;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getNodeType() {
            return nodeType;
        }

        @Override
        public UUID getParentId() {
            return parentId;
        }

        @Override
        public String getSerialNumber() {
            return serialNumber;
        }

        @Override
        public LocalDate getInstallationDate() {
            return installationDate;
        }
    }
}


