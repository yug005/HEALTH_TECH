package com.healthtech.service;

import com.healthtech.dto.AppointmentRequest;
import com.healthtech.dto.AppointmentResponse;
import com.healthtech.entity.Appointment;
import com.healthtech.entity.Doctor;
import com.healthtech.entity.User;
import com.healthtech.exception.ConflictException;
import com.healthtech.exception.ResourceNotFoundException;
import com.healthtech.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Appointment Service - Core business logic for appointment booking.
 * 
 * ============================================
 * CONCURRENCY HANDLING - CRITICAL COMPONENT
 * ============================================
 * 
 * Multi-level strategy to prevent double booking:
 * 
 * LEVEL 1: Application-Level Lock (ReentrantLock)
 * LEVEL 2: Database-Level Pessimistic Lock
 * LEVEL 3: Optimistic Locking (@Version in entity)
 * LEVEL 4: Database Unique Constraint
 */
@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final AuthService authService;

    /**
     * Application-level lock for booking operations.
     * Fair mode ensures threads are processed in order.
     */
    private final ReentrantLock bookingLock = new ReentrantLock(true);

    public AppointmentService(AppointmentRepository appointmentRepository,
            DoctorService doctorService,
            AuthService authService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.authService = authService;
    }

    /**
     * Book an appointment with comprehensive concurrency handling.
     * Transaction Isolation: SERIALIZABLE for maximum consistency.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AppointmentResponse bookAppointment(AppointmentRequest request, String userEmail) {
        log.info("Attempting to book appointment for user: {} with doctor: {} at {}",
                userEmail, request.getDoctorId(), request.getAppointmentTime());

        bookingLock.lock();
        try {
            User patient = authService.getUserByEmail(userEmail);
            Doctor doctor = doctorService.getDoctorEntityById(request.getDoctorId());

            if (!doctor.getIsAvailable()) {
                throw new ConflictException("Doctor is not currently accepting appointments");
            }

            // Check slot availability with pessimistic lock
            Optional<Appointment> existingAppointment = appointmentRepository
                    .findByDoctorAndTimeWithLock(doctor, request.getAppointmentTime());

            if (existingAppointment.isPresent()) {
                log.warn("Slot already booked: Doctor {} at {}",
                        request.getDoctorId(), request.getAppointmentTime());
                throw new ConflictException(
                        "This time slot is already booked. Please choose another time.");
            }

            Appointment appointment = Appointment.builder()
                    .doctor(doctor)
                    .patient(patient)
                    .appointmentTime(request.getAppointmentTime())
                    .status(Appointment.AppointmentStatus.SCHEDULED)
                    .notes(request.getNotes())
                    .build();

            try {
                Appointment savedAppointment = appointmentRepository.save(appointment);
                log.info("Appointment booked successfully with ID: {}", savedAppointment.getId());

                return mapToResponse(savedAppointment);

            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic lock failure during booking", e);
                throw new ConflictException(
                        "Booking conflict detected. The slot was just taken. Please try again.");
            }

        } finally {
            bookingLock.unlock();
        }
    }

    /**
     * Get all appointments for the current user.
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getMyAppointments(String userEmail) {
        User patient = authService.getUserByEmail(userEmail);

        return appointmentRepository.findByPatientOrderByAppointmentTimeDesc(patient)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming appointments for the current user.
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getUpcomingAppointments(String userEmail) {
        User patient = authService.getUserByEmail(userEmail);

        return appointmentRepository.findUpcomingByPatient(patient, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel an appointment.
     */
    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, String userEmail) {
        log.info("Cancelling appointment {} by user {}", appointmentId, userEmail);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + appointmentId));

        if (!appointment.getPatient().getEmail().equals(userEmail)) {
            throw new ConflictException("You can only cancel your own appointments");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
            throw new ConflictException("Only scheduled appointments can be cancelled");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info("Appointment {} cancelled successfully", appointmentId);
        return mapToResponse(savedAppointment);
    }

    /**
     * Get appointment by ID.
     */
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + id));

        return mapToResponse(appointment);
    }

    /**
     * Map Appointment entity to AppointmentResponse DTO.
     */
    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getName())
                .doctorSpecialty(appointment.getDoctor().getSpecialty())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getName())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus().name())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
