package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PagedPatientresponseDTO;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.service.PatientService;
import com.pm.patientservice.validator.CreatePatientValidationGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Patient Service", description = "An API to manage Patients ")
public class PatientController {
    @Autowired
    private PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    @Operation(summary = "Get all patients")
    public ResponseEntity<PagedPatientresponseDTO> getAllPatient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "") String searchValue
    ) {
        // TODO: Implement pagination, sorting, and search in the service layer
   PagedPatientresponseDTO patients = patientService.getAllPatient(page, size, sort, sortField, searchValue);
        return ResponseEntity.ok(patients);
    }

    @PostMapping("/patients")
    @Operation(summary = "Create a new patient")
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}
    ) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok(patientResponseDTO);
    }
    @DeleteMapping("/patients/{id}")
    @Operation(summary = "Delete a patient")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    // here when we update the patient the dataof registered will be validated based on the default group only
    // so it is not a must to pass the registered date in the request body
    @PutMapping("/patients/{id}")
    @Operation(summary = "Update a patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable String id, @Validated({Default.class})
    @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok(patientResponseDTO);
    }
    @GetMapping("/patients/{id}")
    @Operation(summary = "Get a patient by id")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable String id) {
        PatientResponseDTO patientResponseDTO = patientService.getPatientById(id);
        return ResponseEntity.ok(patientResponseDTO);
    }
}
