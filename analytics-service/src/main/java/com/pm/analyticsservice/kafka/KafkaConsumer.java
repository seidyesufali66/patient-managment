package com.pm.analyticsservice.kafka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;
@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    @KafkaListener(topics = "patient-events", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received event: {}", patientEvent);
                 log.info("Patient created event received [Patient ID: {}, Name: {}, Email: {}]",
                         patientEvent.getPatientID(),patientEvent.getName(),patientEvent.getEmail());
        }
        catch (Exception e){
         log.error("Error occurred while deserializing an  event: {}", e.getMessage());
        }
    }
}
