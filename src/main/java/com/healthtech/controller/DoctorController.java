package com.healthtech.controller;

import com.healthtech.dto.ApiResponse;
import com.healthtech.dto.DoctorRequest;
import com.healthtech.dto.DoctorResponse;
import com.healthtech.service.DoctorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Doctor Controller - RESTful API for doctor management.
 */
@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

        private static final Logger log = LoggerFactory.getLogger(DoctorController.class);

        private final DoctorService doctorService;

        public DoctorController(DoctorService doctorService) {
                this.doctorService = doctorService;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors() {
                log.info("Fetching all doctors");

                List<DoctorResponse> doctors = doctorService.getAllDoctors();

                return ResponseEntity.ok(
                                ApiResponse.success(doctors, "Doctors retrieved successfully"));
        }

        @GetMapping("/available")
        public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAvailableDoctors() {
                log.info("Fetching available doctors");

                List<DoctorResponse> doctors = doctorService.getAvailableDoctors();

                return ResponseEntity.ok(
                                ApiResponse.success(doctors, "Available doctors retrieved successfully"));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable Long id) {
                log.info("Fetching doctor with ID: {}", id);

                DoctorResponse doctor = doctorService.getDoctorById(id);

                return ResponseEntity.ok(
                                ApiResponse.success(doctor, "Doctor retrieved successfully"));
        }

        @GetMapping("/specialty/{specialty}")
        public ResponseEntity<ApiResponse<List<DoctorResponse>>> getDoctorsBySpecialty(
                        @PathVariable String specialty) {
                log.info("Fetching doctors with specialty: {}", specialty);

                List<DoctorResponse> doctors = doctorService.getDoctorsBySpecialty(specialty);

                return ResponseEntity.ok(
                                ApiResponse.success(doctors, "Doctors retrieved successfully"));
        }

        @GetMapping("/specialties")
        public ResponseEntity<ApiResponse<List<String>>> getAllSpecialties() {
                log.info("Fetching all specialties");

                List<String> specialties = doctorService.getAllSpecialties();

                return ResponseEntity.ok(
                                ApiResponse.success(specialties, "Specialties retrieved successfully"));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(
                        @Valid @RequestBody DoctorRequest request) {
                log.info("Creating new doctor: {}", request.getName());

                DoctorResponse doctor = doctorService.createDoctor(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.success(doctor, "Doctor created successfully"));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
                        @PathVariable Long id,
                        @Valid @RequestBody DoctorRequest request) {
                log.info("Updating doctor with ID: {}", id);

                DoctorResponse doctor = doctorService.updateDoctor(id, request);

                return ResponseEntity.ok(
                                ApiResponse.success(doctor, "Doctor updated successfully"));
        }

        @PatchMapping("/{id}/availability")
        public ResponseEntity<ApiResponse<DoctorResponse>> setAvailability(
                        @PathVariable Long id,
                        @RequestParam boolean available) {
                log.info("Setting availability for doctor {}: {}", id, available);

                DoctorResponse doctor = doctorService.setAvailability(id, available);

                return ResponseEntity.ok(
                                ApiResponse.success(doctor, "Availability updated successfully"));
        }
}
