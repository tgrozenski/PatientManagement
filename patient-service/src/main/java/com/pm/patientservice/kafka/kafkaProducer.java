package com.pm.patientservice.kafka;

import com.pm.patientservice.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;
import java.util.function.BiConsumer;

@Service
public class kafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(kafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public kafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();

        kafkaTemplate.send("patient", event.toByteArray())
            .whenComplete(new BiConsumer<SendResult<String, byte[]>, Throwable>() {
                @Override
                public void accept(SendResult<String, byte[]> result, Throwable ex) {
                    if (ex == null) {
                        log.info("Successfully sent patient event to Kafka: patientId={}", patient.getId());
                    } else {
                        log.error("Failed to send patient event to Kafka: patientId={}, error={}", 
                            patient.getId(), ex.getMessage());
                        throw new RuntimeException("Kafka send failed", ex);
                    }
                }
            });
    }
}
