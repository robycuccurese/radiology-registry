package it.cyberqual.radiology_registry.api.equipment;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentRequest;
import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentResponse;
import it.cyberqual.radiology_registry.application.equipment.EquipmentService;
import it.cyberqual.radiology_registry.domain.model.EquipmentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.cyberqual.radiology_registry.common.security.SecurityConfig;

/**
 * Integration tests for {@link EquipmentController} security rules.
 *
 * <p>Verifies that POST endpoints are protected by the ADMIN role
 * simulated via the {@code X-User-Role} HTTP header.
 */
@WebMvcTest(EquipmentController.class)
@Import(SecurityConfig.class)
class EquipmentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private EquipmentService equipmentService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @TestConfiguration(proxyBeanMethods = false)
    static class TestAuditConfig {
        @Bean("auditAwareImpl")
        AuditorAware<String> auditAwareImpl() {
            return () -> Optional.of("TEST");
        }
    }

    private static final UUID PARENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    void postEquipment_withAdminRole_shouldReturn200() throws Exception {
        CreateEquipmentRequest request = new CreateEquipmentRequest(
                "GE Revolution CT",
                EquipmentType.CT,
                "GE-REV-001",
                LocalDate.of(2024, 1, 15),
                PARENT_ID
        );

        when(equipmentService.create(any())).thenReturn(
                new CreateEquipmentResponse(
                        UUID.randomUUID(),
                        "GE Revolution CT",
                        EquipmentType.CT,
                        "GE-REV-001",
                        LocalDate.of(2024, 1, 15),
                        PARENT_ID
                )
        );

        mockMvc.perform(post("/api/apparecchiature")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void postEquipment_withoutRoleHeader_shouldReturn403() throws Exception {
        CreateEquipmentRequest request = new CreateEquipmentRequest(
                "GE Revolution CT",
                EquipmentType.CT,
                "GE-REV-001",
                LocalDate.of(2024, 1, 15),
                PARENT_ID
        );

        mockMvc.perform(post("/api/apparecchiature")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void postEquipment_withUserRole_shouldReturn403() throws Exception {
        CreateEquipmentRequest request = new CreateEquipmentRequest(
                "GE Revolution CT",
                EquipmentType.CT,
                "GE-REV-001",
                LocalDate.of(2024, 1, 15),
                PARENT_ID
        );

        mockMvc.perform(post("/api/apparecchiature")
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
