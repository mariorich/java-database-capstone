package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.DTO.Login;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service generalService;

    public DoctorController(DoctorService doctorService, Service generalService) {
        this.doctorService = doctorService;
        this.generalService = generalService;
    }

    // 3. Check doctor availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user,
                                                   @PathVariable Long doctorId,
                                                   @PathVariable String date,
                                                   @PathVariable String token) {
        if (!generalService.validateToken(token, user)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return doctorService.getDoctorAvailability(doctorId, date);
    }

    // 4. Get all doctors
    @GetMapping
    public ResponseEntity<?> getDoctor() {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getAllDoctors());
        return ResponseEntity.ok(response);
    }

    // 5. Save new doctor
    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(@PathVariable String token,
                                        @Valid @RequestBody Doctor doctor) {
        if (!generalService.validateToken(token, "admin")) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }
        return doctorService.saveDoctor(doctor);
    }

    // 6. Doctor login
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@Valid @RequestBody Login login) {
        return doctorService.loginDoctor(login);
    }

    // 7. Update existing doctor
    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(@PathVariable String token,
                                          @Valid @RequestBody Doctor doctor) {
        if (!generalService.validateToken(token, "admin")) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }
        return doctorService.updateDoctor(doctor);
    }

    // 8. Delete doctor by ID
    @DeleteMapping("/{doctorId}/{token}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId,
                                          @PathVariable String token) {
        if (!generalService.validateToken(token, "admin")) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }
        return doctorService.deleteDoctor(doctorId);
    }

    // 9. Filter doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<?> filter(@PathVariable String name,
                                    @PathVariable String time,
                                    @PathVariable String speciality) {
        return generalService.filterDoctors(name, time, speciality);
    }
}
