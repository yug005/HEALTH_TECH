package com.healthtech.config;

import com.healthtech.entity.Doctor;
import com.healthtech.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Data Initializer - Populates sample data on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

        private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

        private final DoctorRepository doctorRepository;

        public DataInitializer(DoctorRepository doctorRepository) {
                this.doctorRepository = doctorRepository;
        }

        @Override
        public void run(String... args) {
                if (doctorRepository.count() == 0) {
                        log.info("Initializing sample doctor data...");

                        List<Doctor> doctors = List.of(
                                        Doctor.builder()
                                                        .name("Dr. Sarah Johnson")
                                                        .specialty("Cardiology")
                                                        .email("sarah.johnson@healthtech.com")
                                                        .phoneNumber("555-0101")
                                                        .yearsOfExperience(15)
                                                        .isAvailable(true)
                                                        .build(),

                                        Doctor.builder()
                                                        .name("Dr. Michael Chen")
                                                        .specialty("Dermatology")
                                                        .email("michael.chen@healthtech.com")
                                                        .phoneNumber("555-0102")
                                                        .yearsOfExperience(10)
                                                        .isAvailable(true)
                                                        .build(),

                                        Doctor.builder()
                                                        .name("Dr. Emily Williams")
                                                        .specialty("Pediatrics")
                                                        .email("emily.williams@healthtech.com")
                                                        .phoneNumber("555-0103")
                                                        .yearsOfExperience(8)
                                                        .isAvailable(true)
                                                        .build(),

                                        Doctor.builder()
                                                        .name("Dr. James Rodriguez")
                                                        .specialty("Orthopedics")
                                                        .email("james.rodriguez@healthtech.com")
                                                        .phoneNumber("555-0104")
                                                        .yearsOfExperience(20)
                                                        .isAvailable(true)
                                                        .build(),

                                        Doctor.builder()
                                                        .name("Dr. Amanda Thompson")
                                                        .specialty("Neurology")
                                                        .email("amanda.thompson@healthtech.com")
                                                        .phoneNumber("555-0105")
                                                        .yearsOfExperience(12)
                                                        .isAvailable(true)
                                                        .build(),

                                        Doctor.builder()
                                                        .name("Dr. Robert Kim")
                                                        .specialty("General Medicine")
                                                        .email("robert.kim@healthtech.com")
                                                        .phoneNumber("555-0106")
                                                        .yearsOfExperience(5)
                                                        .isAvailable(true)
                                                        .build());

                        doctorRepository.saveAll(doctors);
                        log.info("Sample doctors created: {}", doctors.size());
                } else {
                        log.info("Doctors already exist, skipping initialization");
                }
        }
}
