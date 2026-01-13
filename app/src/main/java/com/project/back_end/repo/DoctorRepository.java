package com.project.back_end.repo;
import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d WHERE d.email = :email")
    Doctor findByEmail(@Param("email") String email);

    public Doctor findByUsername(String username);

    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(@Param("name") String name, @Param("specialty") String specialty);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findBySpecialtyIgnoreCase(@Param("specialty") String specialty);

}