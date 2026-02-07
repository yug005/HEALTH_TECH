package com.healthtech.repository;

import com.healthtech.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Doctor Repository - Data access layer for Doctor entity.
 * 
 * Provides specialized queries for doctor management.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find all available doctors.
     * 
     * @return List of doctors accepting appointments
     */
    List<Doctor> findByIsAvailableTrue();

    /**
     * Find doctors by specialty.
     * Case-insensitive search.
     * 
     * @param specialty Doctor's specialty
     * @return List of matching doctors
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

    /**
     * Find doctor by email.
     * 
     * @param email Doctor's email
     * @return Optional containing doctor if found
     */
    Optional<Doctor> findByEmail(String email);

    /**
     * Get all unique specialties in the system.
     * Useful for frontend dropdown population.
     * 
     * @return List of distinct specialties
     */
    @Query("SELECT DISTINCT d.specialty FROM Doctor d")
    List<String> findAllSpecialties();

    /**
     * Search doctors by name (partial match, case-insensitive).
     * 
     * @param name Name to search
     * @return List of matching doctors
     */
    List<Doctor> findByNameContainingIgnoreCase(String name);
}
