package com.healthtech.controller;

import com.healthtech.dto.ApiResponse;
import com.healthtech.dto.AppointmentRequest;
import com.healthtech.dto.AppointmentResponse;
import com.healthtech.service.AppointmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Appointment Controller - RESTful API for appointment booking.
 * All endpoints require authentication (valid JWT token).
 */
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

        private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);

        private final AppointmentService appointmentService;

        public AppointmentController(AppointmentService appointmentService) {
                this.appointmentService = appointmentService;
        }

        @PostMapping
        public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
                        @Valid @RequestBody AppointmentRequest request,
                        Authentication authentication) {

                String userEmail = authentication.getName();
                log.info("Booking appointment for user: {} with doctor: {} at {}",
                                userEmail, request.getDoctorId(), request.getAppointmentTime());

                AppointmentResponse response = appointmentService.bookAppointment(request, userEmail);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.success(response, "Appointment booked successfully"));
        }

        @GetMapping("/my")
        public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(
                        Authentication authentication) {

                String userEmail = authentication.getName();
                log.info("Fetching appointments for user: {}", userEmail);

                List<AppointmentResponse> appointments = appointmentService.getMyAppointments(userEmail);

                return ResponseEntity.ok(
                                ApiResponse.success(appointments, "Appointments retrieved successfully"));
        }

        @GetMapping("/upcoming")
        public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getUpcomingAppointments(
                        Authentication authentication) {

                String userEmail = authentication.getName();
                log.info("Fetching upcoming appointments for user: {}", userEmail);

                List<AppointmentResponse> appointments = appointmentService.getUpcomingAppointments(userEmail);

                return ResponseEntity.ok(
                                ApiResponse.success(appointments, "Upcoming appointments retrieved successfully"));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(
                        @PathVariable Long id) {
                log.info("Fetching appointment with ID: {}", id);

                AppointmentResponse appointment = appointmentService.getAppointmentById(id);

                return ResponseEntity.ok(
                                ApiResponse.success(appointment, "Appointment retrieved successfully"));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
                        @PathVariable Long id,
                        Authentication authentication) {

                String userEmail = authentication.getName();
                log.info("Cancelling appointment {} by user: {}", id, userEmail);

                AppointmentResponse response = appointmentService.cancelAppointment(id, userEmail);

                return ResponseEntity.ok(
                                ApiResponse.success(response, "Appointment cancelled successfully"));
        }
}
