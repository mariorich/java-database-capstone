package com.project.back_end.services;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.TimeSlot;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.stereotype.Component;
import com.project.back_end.services.TokenService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Component
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository, 
                         AppointmentRepository appointmentRepository, 
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public List<String> getDoctorAvaliability(Long doctorId, LocalDate date){
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        List<String> availability = new ArrayList<>();
        if (doctorOpt.isEmpty()) {
            return availability;
        }else{
            List<TimeSlot> availableTimes = doctorOpt.get().getAvailableTimes().stream().map(TimeSlot::new).toList();
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIDNIGHT);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
            List<String> bookedTimes = new ArrayList<>();
            for (Appointment appointment : appointments) {
                String time = appointment.getAppointmentTime().toLocalTime().toString();
                bookedTimes.add(time);
            }

            for (TimeSlot timeSlot : availableTimes) {
                String time = timeSlot.getStartTime();
                if (!bookedTimes.contains(time)) {
                    availability.add(time);
                }
            }
            return availability;
        }
    }

    @Transactional
    public int saveDoctor(Doctor doctor, String token) {

        String cleanToken = token.replace("Bearer ", "");

        if (!tokenService.isTokenValid(cleanToken, "admin")) {
            return 0;
        }

        try {

            Optional<Doctor> existingDoctor =
                    doctorRepository.findByEmail(doctor.getEmail());

            if (existingDoctor.isPresent()) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor, String token) {
        
        String cleanToken = token.replace("Bearer ", "");
        System.out.println("RAW TOKEN: " + token);
        System.out.println("CLEAN TOKEN: " + cleanToken);
        System.out.println("ADMIN VALID: " + tokenService.isTokenValid(cleanToken, "admin"));
        
        if (!tokenService.isTokenValid(cleanToken, "admin")
        && !tokenService.isTokenValid(cleanToken, "doctor")) {
            return 0;
        }

        try{
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctor.getId());
            if(existingDoctor.isEmpty()){
                return -1; 
            }
            doctorRepository.save(doctor);
            return 1; 
        }catch(Exception e){
            return 0; 
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(Long doctorId, String token) {

        String cleanToken = token.replace("Bearer ", "");
        System.out.println("RAW TOKEN: " + token);
        System.out.println("CLEAN TOKEN: " + cleanToken);
        System.out.println("ADMIN VALID: " + tokenService.isTokenValid(cleanToken, "admin"));
        
        if (!tokenService.isTokenValid(cleanToken, "admin")
        && !tokenService.isTokenValid(cleanToken, "doctor")) {
            return 0;
        }
        try{
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctorId);
            if(existingDoctor.isEmpty()){
                return -1; 
            }
            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1; 
        }catch(Exception e){
            return 0; 
        }
    }

    public ResponseEntity<Map<String, Object>> validateDoctor(String email, String password) {
        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (doctor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Doctor not found"));
        }
        if (!doctor.get().getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid password"));
        }
        String token = tokenService.generateToken(doctor.get().getEmail());
        return ResponseEntity.ok(Map.of("token", token, "doctor", doctor));
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        return Map.of("doctors", doctors);
    }

    @Transactional
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        List<Doctor> filteredDoctors = doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes().stream()
                        .map(ts -> ts.split("-")[0])       
                        .map(LocalTime::parse)             
                        .anyMatch(time -> {
                            if ("AM".equalsIgnoreCase(amOrPm)) {
                                return time.isBefore(LocalTime.NOON);
                            } else if ("PM".equalsIgnoreCase(amOrPm)) {
                                return !time.isBefore(LocalTime.NOON); 
                            } else {
                                return true; 
                            }
                        })
                )
                .toList(); 
        return filteredDoctors;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecialtyAndTime(String name, String specialty, String amOrPm) {

        List<Doctor> doctors;
        if ("all".equalsIgnoreCase(specialty)) {
            doctors = doctorRepository.findByNameLike(name);
        } else {
            doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        }

        if (!"all".equalsIgnoreCase(amOrPm)) {
            doctors = filterDoctorByTime(doctors, amOrPm);
        }

        return Map.of("doctors", doctors);
    }   

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", filteredDoctors);
    }   

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecialty(String name, String specialty) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return Map.of("doctors", doctors);
    }

    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecialty(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm); 
        return Map.of("doctors", filteredDoctors);
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpecialty(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return Map.of("doctors", doctors);
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", filteredDoctors);
    }

}