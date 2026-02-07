package com.healthtech.dto;

/**
 * DTO for doctor responses.
 */
public class DoctorResponse {
    private Long id;
    private String name;
    private String specialty;
    private String email;
    private String phoneNumber;
    private Integer yearsOfExperience;
    private Boolean isAvailable;

    public DoctorResponse() {
    }

    public DoctorResponse(Long id, String name, String specialty, String email,
            String phoneNumber, Integer yearsOfExperience, Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.isAvailable = isAvailable;
    }

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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public static DoctorResponseBuilder builder() {
        return new DoctorResponseBuilder();
    }

    public static class DoctorResponseBuilder {
        private Long id;
        private String name;
        private String specialty;
        private String email;
        private String phoneNumber;
        private Integer yearsOfExperience;
        private Boolean isAvailable;

        public DoctorResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DoctorResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DoctorResponseBuilder specialty(String specialty) {
            this.specialty = specialty;
            return this;
        }

        public DoctorResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public DoctorResponseBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public DoctorResponseBuilder yearsOfExperience(Integer yearsOfExperience) {
            this.yearsOfExperience = yearsOfExperience;
            return this;
        }

        public DoctorResponseBuilder isAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public DoctorResponse build() {
            return new DoctorResponse(id, name, specialty, email, phoneNumber, yearsOfExperience, isAvailable);
        }
    }
}
