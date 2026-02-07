package com.healthtech.repository;

import com.healthtech.entity.Appointment;
import com.healthtech.entity.Doctor;
import com.healthtech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Appointment Repository - Data access layer for Appointment entity.
 * 
 * CRITICAL for CONCURRENCY HANDLING:
 * - Contains pessimistic locking queries for double-booking prevention
 * - Uses @Lock annotation for database-level locking
 * 
 * Two-Level Concurrency Strategy:
 * 1. Pessimistic lock when checking slot availability
 * 2. Optimistic lock (@Version in entity) during save
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Check if a time slot is already booked for a specific doctor.
     * 
     * CONCURRENCY: Uses PESSIMISTIC_WRITE lock to prevent race conditions.
     * When this query runs:
     * 1. It locks the matching rows in the database
     * 2. Other transactions must wait until this one commits
     * 3. Prevents two users from booking the same slot simultaneously
     * 
     * @param doctor          Doctor entity
     * @param appointmentTime Time slot to check
     * @return Optional containing appointment if slot is taken
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
            "AND a.appointmentTime = :appointmentTime " +
            "AND a.status NOT IN ('CANCELLED')")
    Optional<Appointment> findByDoctorAndTimeWithLock(
            @Param("doctor") Doctor doctor,
            @Param("appointmentTime") LocalDateTime appointmentTime);

    /**
     * Find all appointments for a patient.
     * 
     * @param patient Patient user
     * @return List of appointments
     */
    List<Appointment> findByPatientOrderByAppointmentTimeDesc(User patient);

    /**
     * Find all appointments for a doctor.
     * 
     * @param doctor Doctor entity
     * @return List of appointments
     */
    List<Appointment> findByDoctorOrderByAppointmentTimeDesc(Doctor doctor);

    /**
     * Find appointments within a date range.
     * Useful for calendar views.
     * 
     * @param start Start of date range
     * @param end   End of date range
     * @return List of appointments in range
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findAppointmentsInRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Check if slot exists (without locking) for quick validation.
     * 
     * @param doctor          Doctor entity
     * @param appointmentTime Time slot
     * @return true if slot is taken
     */
    boolean existsByDoctorAndAppointmentTimeAndStatusNot(
            Doctor doctor,
            LocalDateTime appointmentTime,
            Appointment.AppointmentStatus status);

    /**
     * Find upcoming appointments for a patient.
     * 
     * @param patient Patient user
     * @param now     Current time
     * @return List of future appointments
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND a.appointmentTime > :now " +
            "AND a.status = 'SCHEDULED' " +
            "ORDER BY a.appointmentTime ASC")
    List<Appointment> findUpcomingByPatient(
            @Param("patient") User patient,
            @Param("now") LocalDateTime now);
}
