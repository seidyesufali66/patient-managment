package com.pm.patientservice.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotNull
    private String name;
    @Email
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private String address;
    @NotNull
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @NotNull
    @Column(name = "registered_date")
    private LocalDate registeredDate;
    // getters & setters for ALL fields
}