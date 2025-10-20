package com.pm.patientservice.kafka;
import com.pm.patientservice.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;
@Slf4j
@Service
public class KafkaProducer {
    private  final KafkaTemplate<String,byte[]> kafkaTemplate;
    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendEvent(Patient patient) {
        PatientEvent patientEvent = PatientEvent.newBuilder()
                .setPatientID(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("Patient_Created")
                .build();
        try {
            kafkaTemplate.send("patient-events",patientEvent.toByteArray());
        } catch (Exception e) {
            log.error("Error occurred while sending an event: {}", e.getMessage());
        }
    }
}
