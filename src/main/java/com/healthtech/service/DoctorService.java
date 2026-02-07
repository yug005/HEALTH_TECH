package com.healthtech.service;

import com.healthtech.dto.DoctorRequest;
import com.healthtech.dto.DoctorResponse;
import com.healthtech.entity.Doctor;
import com.healthtech.exception.ResourceNotFoundException;
import com.healthtech.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Doctor Service - Business logic for doctor management.
 * Demonstrates Java 8+ Streams for data transformation.
 */
@Service
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Get all doctors.
     */
    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {
        log.info("Fetching all doctors");

        return doctorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get available doctors only.
     */
    @Transactional(readOnly = true)
    public List<DoctorResponse> getAvailableDoctors() {
        log.info("Fetching available doctors");

        return doctorRepository.findByIsAvailableTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get doctor by ID.
     */
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        log.info("Fetching doctor with ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        return mapToResponse(doctor);
    }

    /**
     * Get doctors by specialty.
     */
    @Transactional(readOnly = true)
    public List<DoctorResponse> getDoctorsBySpecialty(String specialty) {
        log.info("Fetching doctors with specialty: {}", specialty);

        return doctorRepository.findBySpecialtyIgnoreCase(specialty)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a new doctor.
     */
    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        log.info("Creating new doctor: {}", request.getName());

        Doctor doctor = Doctor.builder()
                .name(request.getName())
                .specialty(request.getSpecialty())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .yearsOfExperience(request.getYearsOfExperience())
                .isAvailable(true)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor created with ID: {}", savedDoctor.getId());

        return mapToResponse(savedDoctor);
    }

    /**
     * Update doctor details.
     */
    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        log.info("Updating doctor with ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        doctor.setName(request.getName());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setEmail(request.getEmail());
        doctor.setPhoneNumber(request.getPhoneNumber());
        doctor.setYearsOfExperience(request.getYearsOfExperience());

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor updated successfully");

        return mapToResponse(updatedDoctor);
    }

    /**
     * Toggle doctor availability.
     */
    @Transactional
    public DoctorResponse setAvailability(Long id, boolean available) {
        log.info("Setting availability for doctor {}: {}", id, available);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        doctor.setIsAvailable(available);
        Doctor updatedDoctor = doctorRepository.save(doctor);

        return mapToResponse(updatedDoctor);
    }

    /**
     * Get all specialties.
     */
    @Transactional(readOnly = true)
    public List<String> getAllSpecialties() {
        return doctorRepository.findAllSpecialties();
    }

    /**
     * Get doctor entity by ID (internal use).
     */
    @Transactional(readOnly = true)
    public Doctor getDoctorEntityById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    /**
     * Map Doctor entity to DoctorResponse DTO.
     */
    private DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialty(doctor.getSpecialty())
                .email(doctor.getEmail())
                .phoneNumber(doctor.getPhoneNumber())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .isAvailable(doctor.getIsAvailable())
                .build();
    }
}
