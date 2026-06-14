package it.cyberqual.radiology_registry.domain.validator;

import it.cyberqual.radiology_registry.domain.model.Container;
import it.cyberqual.radiology_registry.domain.model.Equipment;
import it.cyberqual.radiology_registry.domain.model.EquipmentType;
import it.cyberqual.radiology_registry.domain.model.NodeType;
import it.cyberqual.radiology_registry.domain.model.Organization;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HierarchyValidatorTest {

    private final HierarchyValidator validator = new HierarchyValidator();

    @Test
    void shouldAllowOrganizationAsRoot() {
        assertDoesNotThrow(() -> validator.validateParent(null, NodeType.ORGANIZATION));
    }

    @Test
    void shouldRejectNonOrganizationAsRoot() {
        assertThrows(IllegalStateException.class, () -> validator.validateParent(null, NodeType.EQUIPMENT));
    }

    @Test
    void shouldRejectOrganizationAsChild() {
        Organization parent = new Organization(UUID.randomUUID(), "ASL 1");

        assertThrows(IllegalStateException.class, () -> validator.validateParent(parent, NodeType.ORGANIZATION));
    }

    @Test
    void shouldRejectContainerUnderEquipment() {
        Equipment equipmentParent = new Equipment(
                UUID.randomUUID(),
                "Portable XR",
                new Organization(UUID.randomUUID(), "ASL 1"),
                EquipmentType.XRAY,
                "SER-001",
                LocalDate.of(2024, 1, 1),
                UUID.randomUUID()
        );

        assertThrows(IllegalStateException.class, () -> validator.validateParent(equipmentParent, NodeType.CONTAINER));
    }

    @Test
    void shouldAllowContainerUnderOrganization() {
        Organization parent = new Organization(UUID.randomUUID(), "ASL 1");

        assertDoesNotThrow(() -> validator.validateParent(parent, NodeType.CONTAINER));
    }

    @Test
    void shouldRejectEquipmentUnderEquipment() {
        Equipment equipmentParent = new Equipment(
                UUID.randomUUID(),
                "MRI Main",
                new Organization(UUID.randomUUID(), "ASL 1"),
                EquipmentType.MRI,
                "SER-002",
                LocalDate.of(2024, 1, 1),
                UUID.randomUUID()
        );

        assertThrows(IllegalStateException.class, () -> validator.validateParent(equipmentParent, NodeType.EQUIPMENT));
    }

    @Test
    void shouldAllowEquipmentUnderContainer() {
        Organization org = new Organization(UUID.randomUUID(), "ASL 1");
        Container container = new Container(UUID.randomUUID(), "Radiology", org, org.getId());

        assertDoesNotThrow(() -> validator.validateParent(container, NodeType.EQUIPMENT));
    }
}

