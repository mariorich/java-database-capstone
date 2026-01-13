package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

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
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(Map.of("appointments", dtoList));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve appointments"));
        }
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(Long patientId, String condition) {
        try {
            int status;
            if (condition.equalsIgnoreCase("future")) status = 0;
            else if (condition.equalsIgnoreCase("past")) status = 1;
            else return ResponseEntity.badRequest().body(Map.of("error", "Invalid condition. Use 'past' or 'future'."));

            List<Appointment> appointments =
                    appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(Map.of("appointments", dtoList));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error filtering by condition"));
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(Long patientId, String doctorName) {
        try {
            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId);

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(Map.of("appointments", dtoList));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error filtering by doctor name"));
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(Long patientId, String doctorName, String condition) {
        try {
            int status;
            if (condition.equalsIgnoreCase("future")) status = 0;
            else if (condition.equalsIgnoreCase("past")) status = 1;
            else return ResponseEntity.badRequest().body(Map.of("error", "Invalid condition. Use 'past' or 'future'."));

            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(Map.of("appointments", dtoList));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error filtering by doctor and condition"));
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        try {
            String email = tokenService.extractEmail(token);
            Optional<Patient> patientOpt = patientRepository.findByEmail(email);

            if (patientOpt.isPresent()) {
                return ResponseEntity.ok().body(Map.of("patient", patientOpt.get()));
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Patient not found"));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to retrieve patient details"));
        }
    }
}
