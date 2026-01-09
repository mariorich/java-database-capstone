package com.project.back_end.services;

import com.project.back_end.repositories.AdminRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final String jwtSecret; 

    public TokenService(AdminRepository adminRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, @Value("${jwt.secret}") String jwtSecret) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.jwtSecret = jwtSecret;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 604800000L);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody().getSubject();
        } catch (JwtException e) {
            return null; // Invalid token
        }
    }

    public boolean isTokenValid(String token, String userType) {
        try {
            String email = extractEmail(token);
            if (email == null) {
                return false;
            }

            switch (userType.toLowerCase()) {
                case "admin":
                    return adminRepository.findByEmail(email).isPresent();
                case "doctor":
                    return doctorRepository.findByEmail(email).isPresent();
                case "patient":
                    return patientRepository.findByEmail(email).isPresent();
                default:
                    return false;
            }
        } catch (Exception e) {
            return false; // Token is invalid or an error occurred
        }
    }
}
