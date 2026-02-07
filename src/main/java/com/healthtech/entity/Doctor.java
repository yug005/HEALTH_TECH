package com.healthtech.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Doctor Entity - Represents a doctor in the healthcare system.
 */
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialty;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    public Doctor() {
    }

    public Doctor(Long id, String name, String specialty, String email, String phoneNumber,
            Integer yearsOfExperience, Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    // Builder pattern
    public static DoctorBuilder builder() {
        return new DoctorBuilder();
    }

    public static class DoctorBuilder {
        private Long id;
        private String name;
        private String specialty;
        private String email;
        private String phoneNumber;
        private Integer yearsOfExperience;
        private Boolean isAvailable = true;

        public DoctorBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DoctorBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DoctorBuilder specialty(String specialty) {
            this.specialty = specialty;
            return this;
        }

        public DoctorBuilder email(String email) {
            this.email = email;
            return this;
        }

        public DoctorBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public DoctorBuilder yearsOfExperience(Integer yearsOfExperience) {
            this.yearsOfExperience = yearsOfExperience;
            return this;
        }

        public DoctorBuilder isAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public Doctor build() {
            return new Doctor(id, name, specialty, email, phoneNumber, yearsOfExperience, isAvailable);
        }
    }
}
