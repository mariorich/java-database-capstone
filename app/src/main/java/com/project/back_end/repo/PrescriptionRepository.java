package com.project.back_end.repo;
import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    public Prescription findByAppointmentId(Long appointmentId);

}

