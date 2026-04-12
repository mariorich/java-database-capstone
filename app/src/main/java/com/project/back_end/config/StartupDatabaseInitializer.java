package com.project.back_end.config;

import com.project.back_end.models.Admin;
import com.project.back_end.repo.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupDatabaseInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final MongoTemplate mongoTemplate;

    @Value("${app.init.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.init.admin.password:admin123}")
    private String adminPassword;

    public StartupDatabaseInitializer(AdminRepository adminRepository,
                                      MongoTemplate mongoTemplate) {
        this.adminRepository = adminRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        if (!mongoTemplate.collectionExists("prescriptions")) {
            mongoTemplate.createCollection("prescriptions");
        }

        adminRepository.findByEmail(adminEmail)
                .orElseGet(() -> adminRepository.save(new Admin(adminEmail, adminPassword)));
    }
}
