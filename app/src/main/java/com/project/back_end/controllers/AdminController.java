package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;

import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final DoctorService doctorService;
    private final Service service;


    public AdminController(DoctorService doctorService,
                           Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addDoctor(
            @RequestBody Doctor doctor,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {

        int result = doctorService.saveDoctor(doctor, token);

        if (result == 1) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Doctor added successfully"
            ));
        }

        if (result == -1) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", "Doctor already exists"
            ));
        }

        return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "Unauthorized access"
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(
            @PathVariable("id") Long doctorId,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {

        int result = doctorService.deleteDoctor(doctorId, token);

        if (result == 1) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Doctor deleted successfully"
            ));
        }

        if (result == -1) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", "Doctor does not exist"
            ));
        }

        return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "Unauthorized access"
        ));
    }

}

