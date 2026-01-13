package com.project.back_end.services;

import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

// Models
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.models.TimeSlot;
import com.project.back_end.models.Appointment;

// Repositories
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

// Services
import com.project.back_end.services.TokenService;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.DoctorService;

// DTO
import com.project.back_end.DTO.AppointmentDTO;

// Spring & Java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository,
                   PatientRepository patientRepository, PatientService patientService, DoctorService doctorService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public ResponseEntity<String> validateToken(String token, String userType) {
        try {
            if (!tokenService.isTokenValid(token, userType)) {
                return new ResponseEntity<>("Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error validating token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> validateAdmin(Admin admin) {
        try {
            Admin existingAdmin = adminRepository.findByUsername(admin.getUsername());
            if (existingAdmin != null) {
                if (existingAdmin.getPassword().equals(admin.getPassword())) {
                    String token = tokenService.generateToken(admin.getUsername());
                    Map<String, String> response = new HashMap<>();
                    response.put("token", token);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(Map.of("error", "Invalid password"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(Map.of("error", "Admin not found"), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error during admin validation"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> validatePatientLogin(String username, String password) {
        try {
            Patient patient = patientRepository.findByUsername(username);
            if (patient != null) {
                if (patient.getPassword().equals(password)) {
                    String token = tokenService.generateToken(username);
                    return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(Map.of("error", "Invalid password"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(Map.of("error", "Patient not found"), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error during patient login"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctorLogin(String username, String password) {
        try {
            Doctor doctor = doctorRepository.findByUsername(username);
            if (doctor != null) {
                if (doctor.getPassword().equals(password)) {
                    String token = tokenService.generateToken(username);
                    return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(Map.of("error", "Invalid password"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(Map.of("error", "Doctor not found"), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error during doctor login"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Doctor> filterDoctor(String name, String specialty, String timeSlot) {
        if (name != null && specialty != null && timeSlot != null) {
            return doctorService.filterDoctorsByNameAndSpecilityAndTime(name, specialty, timeSlot);
        } else {
            return doctorRepository.findAll();
        }
    }

    @Transactional
    public int validateAppointment(Long doctorId, String appointmentDate, String appointmentTime) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return -1;

        List<String> availableSlots = doctorService.getDoctorAvaliability(doctorId, appointmentDate);
        LocalTime requestedTime = LocalTime.parse(appointmentTime, DateTimeFormatter.ofPattern("HH:mm"));

        for (String slotStr : availableSlots) {
            TimeSlot slot = new TimeSlot(slotStr);
            LocalTime slotStart = LocalTime.parse(slot.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
            if (slotStart.equals(requestedTime)) return 1;
        }

        return 0;
    }

    @Transactional
    public boolean validatePatient(String email, String phoneNumber) {
        Patient patient = patientRepository.findByEmailOrPhone(email, phoneNumber);
        return patient == null;
    }

    public List<AppointmentDTO> filterPatient(String token, String condition, String doctorName) {
        String username = tokenService.extractUsername(token);
        if (username == null) return Collections.emptyList();

        Optional<Patient> patientOpt = patientRepository.findByUsername(username);
        if (patientOpt.isEmpty()) return Collections.emptyList();

        Patient patient = patientOpt.get();

        List<Appointment> appointments;
        if (condition != null && doctorName != null) {
            appointments = patientService.filterByDoctorAndCondition(patient.getId(), condition.toLowerCase(), doctorName);
        } else if (condition != null) {
            appointments = patientService.filterByCondition(patient.getId(), condition.toLowerCase());
        } else if (doctorName != null) {
            appointments = patientService.filterByDoctor(patient.getId(), doctorName);
        } else {
            appointments = patientService.getAllAppointments(patient.getId());
        }

        return appointments.stream()
                .map(AppointmentDTO::new)
                .toList();
    }

}
