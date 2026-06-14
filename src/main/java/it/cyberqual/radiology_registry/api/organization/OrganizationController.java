package it.cyberqual.radiology_registry.api.organization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cyberqual.radiology_registry.api.organization.dto.NodeTreeDto;

import it.cyberqual.radiology_registry.application.organization.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for organization hierarchy.
 */
@RestController
@RequestMapping("/api/organizzazioni")
@Tag(name = "Organization API", description = "Hierarchy management APIs")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Returns the full hierarchical tree of an organization.
     */
    @Operation(
            summary = "Get organization tree",
            description = "Returns full hierarchy: Organization → Containers → Equipment"
    )
    @GetMapping("/{id}/tree")
    public NodeTreeDto getTree(@PathVariable UUID id) {
        return organizationService.getTree(id);
    }
}