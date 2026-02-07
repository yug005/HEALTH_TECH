package com.healthtech.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Appointment Entity - Represents a booked appointment.
 * 
 * IMPORTANT: This entity is central to the concurrency handling mechanism.
 * Uses @Version for optimistic locking to prevent double booking.
 */
@Entity
@Table(name = "appointments", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "doctor_id", "appointment_time" })
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * VERSION FIELD - Critical for Concurrency Control!
     * JPA automatically increments this on each update.
     */
    @Version
    private Long version;

    @Column(length = 500)
    private String notes;

    public Appointment() {
    }

    public Appointment(Long id, Doctor doctor, User patient, LocalDateTime appointmentTime,
            AppointmentStatus status, LocalDateTime createdAt, String notes) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.createdAt = createdAt;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = AppointmentStatus.SCHEDULED;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public enum AppointmentStatus {
        SCHEDULED, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW
    }

    // Builder pattern
    public static AppointmentBuilder builder() {
        return new AppointmentBuilder();
    }

    public static class AppointmentBuilder {
        private Long id;
        private Doctor doctor;
        private User patient;
        private LocalDateTime appointmentTime;
        private AppointmentStatus status;
        private LocalDateTime createdAt;
        private String notes;

        public AppointmentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AppointmentBuilder doctor(Doctor doctor) {
            this.doctor = doctor;
            return this;
        }

        public AppointmentBuilder patient(User patient) {
            this.patient = patient;
            return this;
        }

        public AppointmentBuilder appointmentTime(LocalDateTime appointmentTime) {
            this.appointmentTime = appointmentTime;
            return this;
        }

        public AppointmentBuilder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public AppointmentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AppointmentBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Appointment build() {
            return new Appointment(id, doctor, patient, appointmentTime, status, createdAt, notes);
        }
    }
}
