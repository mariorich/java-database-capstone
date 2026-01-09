package com.project.back_end.services;
import org.springframework.stereotype.Service;
// Model Imports
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.models.TimeSlot;
import com.project.back_end.models.Appointment;
// Repository Imports
import com.project.back_end.repositories.AdminRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;
// Service Imports
import com.project.back_end.services.TokenService;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.DoctorService;
// Other Imports
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import java.util.Optional;


@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, PatientService patientService, DoctorService doctorService) {
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

    public ResponseEntity<Map<String, String>> validateAdmin(Admin admin) {
        try {
            Optional<Admin> existingAdmin = adminRepository.findByEmail(admin.getEmail());
            if (existingAdmin.isPresent()) {
                if (existingAdmin.get().getPassword().equals(admin.getPassword())) {
                    String token = tokenService.generateToken(admin.getEmail());
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

    public List<Doctor> filterDoctor(String name, String specialty, String timeSlot) {
        if (name != null && specialty != null && timeSlot != null) {
            return doctorService.filterByNameSpecialtyAndTimeSlot(name, specialty, timeSlot);
        } else {
            return doctorRepository.findAll();
        }
    }

    public int validateAppointment(Long doctorId, String appointmentDate, String appointmentTime) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            List<TimeSlot> availableSlots = doctorService.getAvailableTimeSlots(doctorId, appointmentDate);
            for (TimeSlot slot : availableSlots) {
                if (slot.getStartTime().equals(appointmentTime)) {
                    return 1; // Valid appointment time
                }
            }
            return 0; // Invalid appointment time
        } else {
            return -1; // Doctor does not exist
        }
    }

    public boolean validatePatient(String email, String phoneNumber) {
        Optional<Patient> byEmail = patientRepository.findByEmail(email);
        Optional<Patient> byPhone = patientRepository.findByPhoneNumber(phoneNumber);
        return byEmail.isEmpty() && byPhone.isEmpty();
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(String email, String password) {
        try {
            Optional<Patient> patientOpt = patientRepository.findByEmail(email);
            if (patientOpt.isPresent()) {
                Patient patient = patientOpt.get();
                if (patient.getPassword().equals(password)) {
                    String token = tokenService.generateToken(email);
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

    public List<Appointment> filterPatient(String token, String condition, String doctorName) {
        String email = tokenService.extractEmail(token);
        if (email == null) {
            return Collections.emptyList(); // Invalid token
        }

        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        if (patientOpt.isEmpty()) {
            return Collections.emptyList(); // Patient not found
        }

        Patient patient = patientOpt.get();

        if (condition != null && doctorName != null) {
            return patientService.filterByConditionAndDoctor(patient.getId(), condition, doctorName);
        } else if (condition != null) {
            return patientService.filterByCondition(patient.getId(), condition);
        } else if (doctorName != null) {
            return patientService.filterByDoctor(patient.getId(), doctorName);
        } else {
            return patientService.getAllAppointments(patient.getId());
        }
    }
}
