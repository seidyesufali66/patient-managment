package com.pm.patientservice.service;

import com.pm.patientservice.dto.PagedPatientresponseDTO;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;


public interface PatientService {
    public PagedPatientresponseDTO getAllPatient(int page, int size, String sort, String sortField, String searchValue);
    PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO);
    PatientResponseDTO updatePatient(String id, PatientRequestDTO patientRequestDTO);
    void deletePatient(String id);
    PatientResponseDTO getPatientById(String id);
}
