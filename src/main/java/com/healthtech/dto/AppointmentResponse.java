package com.healthtech.dto;

import java.time.LocalDateTime;

/**
 * DTO for appointment responses.
 */
public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private Long patientId;
    private String patientName;
    private LocalDateTime appointmentTime;
    private String status;
    private String notes;
    private LocalDateTime createdAt;

    public AppointmentResponse() {
    }

    public AppointmentResponse(Long id, Long doctorId, String doctorName, String doctorSpecialty,
            Long patientId, String patientName, LocalDateTime appointmentTime,
            String status, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.patientId = patientId;
        this.patientName = patientName;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static AppointmentResponseBuilder builder() {
        return new AppointmentResponseBuilder();
    }

    public static class AppointmentResponseBuilder {
        private Long id;
        private Long doctorId;
        private String doctorName;
        private String doctorSpecialty;
        private Long patientId;
        private String patientName;
        private LocalDateTime appointmentTime;
        private String status;
        private String notes;
        private LocalDateTime createdAt;

        public AppointmentResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AppointmentResponseBuilder doctorId(Long doctorId) {
            this.doctorId = doctorId;
            return this;
        }

        public AppointmentResponseBuilder doctorName(String doctorName) {
            this.doctorName = doctorName;
            return this;
        }

        public AppointmentResponseBuilder doctorSpecialty(String doctorSpecialty) {
            this.doctorSpecialty = doctorSpecialty;
            return this;
        }

        public AppointmentResponseBuilder patientId(Long patientId) {
            this.patientId = patientId;
            return this;
        }

        public AppointmentResponseBuilder patientName(String patientName) {
            this.patientName = patientName;
            return this;
        }

        public AppointmentResponseBuilder appointmentTime(LocalDateTime appointmentTime) {
            this.appointmentTime = appointmentTime;
            return this;
        }

        public AppointmentResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AppointmentResponseBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public AppointmentResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AppointmentResponse build() {
            return new AppointmentResponse(id, doctorId, doctorName, doctorSpecialty, patientId, patientName,
                    appointmentTime, status, notes, createdAt);
        }
    }
}
