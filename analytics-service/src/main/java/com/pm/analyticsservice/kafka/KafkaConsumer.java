package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @RetryableTopic(
        attempts = "4",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics="patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {

        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received Patient Event: [PatientId={}, PatientName={},"
                + "PatientEmail={} ]",
                patientEvent.getPatientId(),
                patientEvent.getName(),
                patientEvent.getEmail());
            
            processPatientEvent(patientEvent);
        }
        catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event: {}", e.getMessage());
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    @KafkaListener(topics = "patient-dlt", groupId = "analytics-service")
    public void handleDlt(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.error("MESSAGE IN DLQ - Could not process after retries. PatientId={}, Name={}, Email={}",
                patientEvent.getPatientId(),
                patientEvent.getName(),
                patientEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Could not deserialize DLQ message: {}", e.getMessage());
        }
    }

    private void processPatientEvent(PatientEvent patientEvent) {
        log.info("Processing patient event for analytics: {}", patientEvent.getPatientId());
    }
}