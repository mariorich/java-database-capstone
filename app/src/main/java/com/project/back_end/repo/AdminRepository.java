package com.project.back_end.repo;

import com.project.back_end.models.Admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("SELECT d FROM Admin d WHERE d.email = :email")
    public Optional<Admin> findByEmail(@Param("email") String email);

}
