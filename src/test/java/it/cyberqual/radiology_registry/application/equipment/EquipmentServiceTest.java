package it.cyberqual.radiology_registry.application.equipment;

import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentRequest;
import it.cyberqual.radiology_registry.domain.model.Container;
import it.cyberqual.radiology_registry.domain.model.EquipmentType;
import it.cyberqual.radiology_registry.domain.model.Node;
import it.cyberqual.radiology_registry.domain.model.Organization;
import it.cyberqual.radiology_registry.domain.validator.HierarchyValidator;
import it.cyberqual.radiology_registry.repository.NodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    private static final Logger log = LoggerFactory.getLogger(EquipmentServiceTest.class);

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private HierarchyValidator hierarchyValidator;

    private EquipmentService equipmentService;

    @BeforeEach
    void setUp() {
        log.debug("Test setup: initializing EquipmentService with repository and validator mocks");
        equipmentService = new EquipmentService(nodeRepository, hierarchyValidator);
    }

    @Test
    void shouldCreateEquipmentWithRootOrgIdCopiedFromParent() {
        log.info("Test started: equipment creation copies rootOrganizationId from parent");
        UUID orgId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID parentContainerId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        Organization org = new Organization(orgId, "ASL 1");
        Container parentContainer = new Container(parentContainerId, "Radiology", org, orgId);

        CreateEquipmentRequest request = new CreateEquipmentRequest(
                "GE Revolution CT",
                EquipmentType.CT,
                "GE-REV-001",
                LocalDate.of(2024, 1, 15),
                parentContainerId
        );

        when(nodeRepository.findById(parentContainerId)).thenReturn(Optional.of(parentContainer));
        when(nodeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        equipmentService.create(request);
        log.debug("Create invocation completed, verifying saved node");

        ArgumentCaptor<Node> equipmentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeRepository).save(equipmentCaptor.capture());

        // Verify that root_org_id was copied from parent
        assertEquals(orgId, equipmentCaptor.getValue().getRootOrganizationId());
        assertEquals("GE Revolution CT", equipmentCaptor.getValue().getName());
        log.info("Test completed successfully: rootOrganizationId copied correctly");
    }

    @Test
    void shouldThrowWhenParentNotFound() {
        log.info("Test started: exception expected when parent does not exist");
        UUID parentId = UUID.fromString("99999999-9999-9999-9999-999999999999");

        CreateEquipmentRequest request = new CreateEquipmentRequest(
                "TAC Mobile",
                EquipmentType.CT,
                "MOBILE-CT-001",
                LocalDate.of(2024, 3, 20),
                parentId
        );

        when(nodeRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> equipmentService.create(request));
        log.info("Test completed successfully: IllegalArgumentException thrown as expected");
    }
}

