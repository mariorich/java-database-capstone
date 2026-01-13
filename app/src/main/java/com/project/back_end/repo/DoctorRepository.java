package com.project.back_end.repo;
import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d WHERE d.email = :email")
    Optional<Doctor> findByEmail(@Param("email") String email);

    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.speciality) = LOWER(:speciality)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(@Param("name") String name, @Param("speciality") String speciality);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.speciality) = LOWER(:speciality)")
    List<Doctor> findBySpecialityIgnoreCase(@Param("speciality") String speciality);

}