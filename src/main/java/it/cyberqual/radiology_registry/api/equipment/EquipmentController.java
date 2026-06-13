package it.cyberqual.radiology_registry.api.equipment
;

import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentRequest;
import it.cyberqual.radiology_registry.api.equipment.dto.EquipmentResponse;
import it.cyberqual.radiology_registry.application.equipment.CreateEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for radiology equipment management.
 */
@RestController
@RequestMapping("/api/apparecchiature")
@RequiredArgsConstructor
public class EquipmentController {

    private final CreateEquipmentService createEquipmentService;

    /**
     * Create a new radiology equipment.
     */
    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@RequestBody CreateEquipmentRequest request) {
        return ResponseEntity.ok(createEquipmentService.create(request));
    }
}