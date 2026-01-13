package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repositories.PrescriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public ResponseEntity<?> savePrescription(Prescription prescription) {
        try {
            Optional<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (existing.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Prescription already exists for this appointment.");
            }

            prescriptionRepository.save(prescription);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Prescription saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving prescription.");
        }
    }

    @Transactional
    public ResponseEntity<?> getPrescription(Long appointmentId) {
        try {
            Optional<Prescription> prescription = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescription.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No prescription found for this appointment.");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("prescription", prescription.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prescription.");
        }
    }
}

