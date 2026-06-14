package it.cyberqual.radiology_registry.api.equipment
;

import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentRequest;
import it.cyberqual.radiology_registry.api.equipment.dto.CreateEquipmentResponse;
import it.cyberqual.radiology_registry.application.equipment.EquipmentService;
import jakarta.validation.Valid;
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

    private final EquipmentService equipmentService;

    /**
     * Create a new radiology equipment.
     */
    @PostMapping
    public ResponseEntity<CreateEquipmentResponse> create(@Valid @RequestBody CreateEquipmentRequest request) {
        return ResponseEntity.ok(equipmentService.create(request));
    }
}