package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service generalService;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service generalService,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.generalService = generalService;
        this.appointmentService = appointmentService;
    }

    // 3. Save a new prescription
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(@PathVariable String token,
                                              @Valid @RequestBody Prescription prescription) {
        if (!generalService.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        // Update appointment status before saving the prescription
        appointmentService.markPrescriptionAdded(prescription.getAppointmentId());

        return prescriptionService.savePrescription(prescription);
    }

    // 4. Get prescription by appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId,
                                             @PathVariable String token) {
        if (!generalService.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}
