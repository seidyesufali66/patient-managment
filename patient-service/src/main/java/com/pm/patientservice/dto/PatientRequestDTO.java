package com.pm.patientservice.dto;
import com.pm.patientservice.validator.CreatePatientValidationGroup;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequestDTO {
    @NotBlank(message = "Name should not be empty")
    @Size(max=100 ,message = "Name should be less than 100 characters")
    private String name;

    @NotBlank(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address should not be empty")
    private String address;

    @NotNull(message = "Date of birth should not be empty")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(groups = CreatePatientValidationGroup.class, message = "Registered date should not be empty")
    @PastOrPresent(message = "Registered date should be in the past or today")
    private LocalDate registeredDate;
}
