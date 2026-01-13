package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.services.TokenService;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final Service service;

    public AppointmentService(AppointmentRepository appointmentRepository, 
                              TokenService tokenService, 
                              PatientRepository patientRepository, 
                              DoctorRepository doctorRepository, 
                              Service service) {

        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.service = service;
    }

    @Transactional
    public ResponseEntity<Map<String, String>> bookAppointment(Appointment appointment, String token) {
    try {
        appointmentRepository.save(appointment);
        return ResponseEntity.ok(Map.of("message", "Appointment booked successfully"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Map.of("message", "Failed to book appointment"));
    }
    }
     
    @Transactional
    public ResponseEntity<Map<String,String>> updateAppointment(Appointment appointment,String token) {
        Appointment existingAppointment = appointmentRepository.findById(appointment.getId()).orElse(null);
        if(existingAppointment != null){
            
            int isValid = service.validateAppointment(appointment.getDoctor().getId(), 
                                                     appointment.getAppointmentDate().toString(), 
                                                     appointment.getAppointmentTimeOnly().toString());

            if(isValid == 1) {
            
                existingAppointment.setAppointmentTime(appointment.getAppointmentTime());
                appointmentRepository.save(existingAppointment);
            
                Map<String,String> response = new HashMap<>();
                response.put("message","Appointment updated successfully");
                return ResponseEntity.ok(response);
            
            } else if (isValid == 0) {
            
                Map<String,String> response = new HashMap<>();
                response.put("message","Invalid appointment time");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            } else if(isValid == -1){
            
                Map<String,String> response = new HashMap<>();
                response.put("message","Doctor does not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        }else {
        
            Map<String,String> response = new HashMap<>();
            response.put("message","Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        Optional<Patient> patient = patientRepository.findByEmail(tokenService.extractEmail(token));
        Long patientId = patient.get().getId();
        if (appointmentOpt.isPresent()) {

            if (!appointmentOpt.get().getPatient().getId().equals(patientId)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Unauthorized to cancel this appointment");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            appointmentRepository.delete(appointmentOpt.get());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);

        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    public Map<String, Object> getAppointment(String pname, String date, String token) {
        String email = tokenService.extractEmail(token);
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);
        Long doctorId = doctorOpt.get().getId();
        LocalDate localDate = LocalDate.parse(date);
        List<Appointment> appointments;
        if (pname != null) {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, pname, localDate.atStartOfDay(), localDate.atStartOfDay().plusDays(1));
        } else {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctorId, localDate.atStartOfDay(), localDate.atStartOfDay().plusDays(1));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);
        return response;
    }
    
}
