package com.project.back_end.controllers;

import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.DTO.Login;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service generalService;

    public PatientController(PatientService patientService, Service generalService) {
        this.patientService = patientService;
        this.generalService = generalService;
    }

    // 3. Get patient details
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        if (!generalService.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return patientService.getPatientDetails(token);
    }

    // 4. Create a new patient
    @PostMapping
    public ResponseEntity<?> createPatient(@Valid @RequestBody Patient patient) {
        if (generalService.patientExists(patient.getEmail())) {
            return ResponseEntity.status(409).body("Patient already exists");
        }
        int result = patientService.createPatient(patient);
        if (result == 1) {
            return ResponseEntity.status(201).body("Patient created successfully");
        } else {
            return ResponseEntity.status(500).body("Error creating patient");
        }
    }

    // 5. Patient login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        return generalService.validatePatientLogin(login);
    }

    // 6. Get patient appointments
    @GetMapping("/appointments/{patientId}/{token}/{role}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long patientId,
                                                   @PathVariable String token,
                                                   @PathVariable String role) {
        if (!generalService.validateToken(token, role)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return patientService.getPatientAppointment(patientId);
    }

    // 7. Filter patient appointments
    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(@PathVariable String condition,
                                                      @PathVariable String name,
                                                      @PathVariable String token) {
        if (!generalService.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return generalService.filterAppointments(condition, name);
    }
}
