package com.pm.patientservice.service.impl;

import com.pm.patientservice.dto.PagedPatientresponseDTO;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExitsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingserviceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.Patientrepository;
import com.pm.patientservice.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class PatientServiceImpl implements PatientService {
    private final KafkaProducer kafkaProducer;
 private Patientrepository patientrepository;
 private final BillingserviceGrpcClient billingserviceGrpcClient;
 public PatientServiceImpl(KafkaProducer kafkaProducer, Patientrepository patientrepository,
                           BillingserviceGrpcClient billingserviceGrpcClient) {
     this.kafkaProducer = kafkaProducer;
     this.patientrepository = patientrepository;
        this.billingserviceGrpcClient = billingserviceGrpcClient;
    }
    @Override
    public PagedPatientresponseDTO getAllPatient(int page, int size, String sort, String sortField, String searchValue) {
      // since the page index is zero-based in Spring Data JPA, we subtract 1 from the provided page number
     Pageable pageable = PageRequest.of(page-1, size,
              sort.equalsIgnoreCase("asc")
                      ? Sort.by(sortField).ascending()
                      : Sort.by(sortField).descending());
      Page<Patient> patientPage;
        if (searchValue == null || searchValue.isBlank()) {
            patientPage = patientrepository.findAll(pageable);
        }
        else
        {
            patientPage = patientrepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    searchValue, searchValue, pageable);
        }

        List<PatientResponseDTO> patientDTOs = patientPage.getContent().stream()
                .map(PatientMapper::toDTO)
                .toList();

        return new PagedPatientresponseDTO(
                patientDTOs,
                patientPage.getNumber(),
                patientPage.getSize(),
                patientPage.getTotalPages(),
                (int) patientPage.getTotalElements()
        );
//       List<Patient> patients= patientrepository.findAll();
//        return patients.stream().map(PatientMapper::toDTO).toList();
    }
    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientrepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExitsException("a patient with this email already exist"
                    + patientRequestDTO.getEmail());
        }
        Patient patient= patientrepository.save(PatientMapper.toModel(patientRequestDTO));
      // call the billing service
        billingserviceGrpcClient.createBillingAccount(patient.getId().toString(),patient.getName(),patient.getEmail());
       //send the kafka event
        kafkaProducer.sendEvent(patient);
        return PatientMapper.toDTO(patient);
    }
    public PatientResponseDTO updatePatient(String id, PatientRequestDTO patientRequestDTO){
    Patient patient =patientrepository.findById(UUID.fromString(id)).orElseThrow(()
            ->new PatientNotFoundException("patient not found",id));
    if(patientrepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), UUID.fromString(id))){
        throw new EmailAlreadyExitsException("a patient with this email already exist"
                + patientRequestDTO.getEmail());
    }
          patient.setName(patientRequestDTO.getName());
          patient.setDateOfBirth(patientRequestDTO.getDateOfBirth());
          patient.setAddress(patientRequestDTO.getAddress());
         Patient saveddPatient=patientrepository.save(patient);
        return PatientMapper.toDTO(saveddPatient);
    }
    public void deletePatient(String id) {
     UUID uuid=UUID.fromString(id);
    Optional<Patient> patient=patientrepository.findById(uuid);
        if (patient.isPresent()) {
            patientrepository.deleteById(UUID.fromString(id));
        } else {
            throw new PatientNotFoundException("patient not found", id);
        }
    }
    @Override
    public PatientResponseDTO getPatientById(String id) {
        Optional<Patient> patient=patientrepository.findById(UUID.fromString(id));
        if (patient.isEmpty()) {
            throw new PatientNotFoundException("patient not found", id);
        }
        return PatientMapper.toDTO(patient.get());
    }
}
