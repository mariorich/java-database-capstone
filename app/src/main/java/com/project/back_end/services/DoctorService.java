package com.project.back_end.services;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.TimeSlot;
import com.project.back_end.models.Appointment;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.DoctorRepository;
import org.springframework.stereotype.Service;
import com.project.back_end.services.TokenService;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;


@Service
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
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        List<String> availability = new ArrayList<>();
        if (doctor.isEmpty()) {
            return availability;
        }else{
            List<TimeSlot> availableTimes = doctor.get().getAvailableTimes();
            List<String> bookedTimes = appointmentRepository.findBookedTimesByDoctorAndDate(doctorId, date);

            for (TimeSlot timeSlot : availableTimes) {
                String time = timeSlot.getTime();
                if (!bookedTimes.contains(time)) {
                    availability.add(time);
                }
            }
            return availability;
        }
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try{
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctor.getId());
            if(existingDoctor.isPresent()){
                return -1; 
            }
            doctorRepository.save(doctor);
            return 1; 
        }catch(Exception e){
            return 0; 
        }
    }   

    @Transactional
    public int updateDoctor(Doctor doctor) {
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
    public int deleteDoctor(Long doctorId) {
        try{
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctorId);
            if(existingDoctor.isEmpty()){
                return -1; 
            }
            appointmentRepository.deleteByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1; 
        }catch(Exception e){
            return 0; 
        }
    }

    public Map<String, Object> validateDoctor(String email, String password) {
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null) {
            return Map.of("message", "Doctor not found");
        }
        if (!doctor.getPassword().equals(password)) {
            return Map.of("message", "Invalid password");
        }
        String token = tokenService.generateToken(doctor.getEmail());
        return Map.of("token", token, "doctor", doctor);
    }

    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        for (Doctor doctor : doctors) {
            doctor.getAvailableTimes().size(); 
        }
        return doctors;
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndSpecilityAndTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        List<Doctor> filteredDoctors = new ArrayList<>();
        for (Doctor doctor : doctors) {
            if (doctor.getSpecialty().equalsIgnoreCase(specialty)) {
                if (doctor.getAvailableTimes().contains(amOrPm)) {
                    filteredDoctors.add(doctor);
                }
            }
        }
        return filteredDoctors;
    }

    // Aditional methods to help
    private boolean isMorningTime(String time) {
        LocalTime localTime = LocalTime.parse(time);
        return localTime.isBefore(LocalTime.NOON);
    }

    private boolean isAfternoonTime(String time) {
        LocalTime localTime = LocalTime.parse(time);
        return localTime.isAfter(LocalTime.NOON) || localTime.equals(LocalTime.NOON);
    }

    @Transactional
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        List<Doctor> filteredDoctors = new ArrayList<>();
        for (Doctor doctor : doctors) {
            for (TimeSlot timeSlot : doctor.getAvailableTimes()) {
                String time = timeSlot.getTime();
                if (amOrPm.equalsIgnoreCase("AM") && isMorningTime(time)) {
                    filteredDoctors.add(doctor);
                    break;
                } else if (amOrPm.equalsIgnoreCase("PM") && isAfternoonTime(time)) {
                    filteredDoctors.add(doctor);
                    break;
                }
            }
        }
        return filteredDoctors;
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        List<Doctor> filteredDoctors = new ArrayList<>();
        for (Doctor doctor : doctors) {
            for (TimeSlot timeSlot : doctor.getAvailableTimes()) {
                String time = timeSlot.getTime();
                if (amOrPm.equalsIgnoreCase("AM") && isMorningTime(time)) {
                    filteredDoctors.add(doctor);
                    break;
                } else if (amOrPm.equalsIgnoreCase("PM") && isAfternoonTime(time)) {
                    filteredDoctors.add(doctor);
                    break;
                }
            }
        }
        return filteredDoctors;
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
    }

    @Transactional
    public List<Doctor> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        List<Doctor> filteredDoctors = new ArrayList<>();
        for (Doctor doctor : doctors) {
            for (TimeSlot timeSlot : doctor.getAvailableTimes()) {
                String time = timeSlot.getTime();
                if (amOrPm.equalsIgnoreCase("AM") && isMorningTime(time)) {
                    filteredDoctors.add(doctor);
                    break;
                } else if (amOrPm.equalsIgnoreCase("PM") && isAfternoonTime(time)) {
                    filteredDoctors.add(doctor);
                    break;
                }
            }
        }
        return filteredDoctors;
    }

    @Transactional
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    @Transactional
    public List<Doctor> filterDoctorsByTime(String amOrPm) {
        List<Doctor> allDoctors = doctorRepository.findAll();
        List<Doctor> filteredDoctors = new ArrayList<>();
        for (Doctor doctor : allDoctors) {
            for (TimeSlot timeSlot : doctor.getAvailableTimes()) {
                String time = timeSlot.getTime();
                if (amOrPm.equalsIgnoreCase("AM") && isMorningTime(time)) {
                    filteredDoctors.add(doctor);
                    break;
                } else if (amOrPm.equalsIgnoreCase("PM") && isAfternoonTime(time)) {
                    filteredDoctors.add(doctor);
                    break;
                }
            }
        }
        return filteredDoctors;
    }
}