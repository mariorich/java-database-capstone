package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.DTO.Login;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 3. Get appointments by date and patient name
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String patientName,
                                             @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return appointmentService.getAppointmentsByDateAndPatient(date, patientName);
    }

    // 4. Book a new appointment
    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(@PathVariable String token,
                                             @Valid @RequestBody Appointment appointment) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return appointmentService.bookAppointment(appointment);
    }

    // 5. Update an existing appointment
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(@PathVariable String token,
                                               @Valid @RequestBody Appointment appointment) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return appointmentService.updateAppointment(appointment);
    }

    // 6. Cancel an appointment
    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId,
                                               @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return appointmentService.cancelAppointment(appointmentId);
    }
}

