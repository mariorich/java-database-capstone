package com.project.back_end.services;

import com.project.back_end.dto.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.PatientRepository;
import com.project.back_end.security.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<?> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve appointments");
        }
    }

    public ResponseEntity<?> filterByCondition(Long patientId, String condition) {
        try {
            int status;
            if (condition.equalsIgnoreCase("future")) status = 0;
            else if (condition.equalsIgnoreCase("past")) status = 1;
            else return ResponseEntity.badRequest().body("Invalid condition. Use 'past' or 'future'.");

            List<Appointment> appointments =
                    appointmentRepository.findByPatientIdAndStatus(patientId, status);

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error filtering by condition");
        }
    }

    public ResponseEntity<?> filterByDoctor(Long patientId, String doctorName) {
        try {
            List<Appointment> appointments =
                    appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, doctorName);

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error filtering by doctor");
        }
    }

    public ResponseEntity<?> filterByDoctorAndCondition(Long patientId, String doctorName, String condition) {
        try {
            int status;
            if (condition.equalsIgnoreCase("future")) status = 0;
            else if (condition.equalsIgnoreCase("past")) status = 1;
            else return ResponseEntity.badRequest().body("Invalid condition. Use 'past' or 'future'.");

            List<Appointment> appointments =
                    appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, doctorName, status);

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error filtering by doctor and condition");
        }
    }

    public ResponseEntity<?> getPatientDetails(String token) {
        try {
            String email = tokenService.extractEmail(token);
            Optional<Patient> patientOpt = patientRepository.findByEmail(email);
            return patientOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body("Patient not found"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve patient details");
        }
    }
}
