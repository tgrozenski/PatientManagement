package com.pm.notificationservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
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
    @KafkaListener(topics = "patient", groupId = "notification-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            sendNotification(patientEvent);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event: {}", e.getMessage());
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    @KafkaListener(topics = "patient-dlt", groupId = "notification-service")
    public void handleDlt(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.error("DLQ: Could not send notification for patient: {}",
                patientEvent.getPatientId());
        } catch (InvalidProtocolBufferException e) {
            log.error("Could not deserialize DLQ message: {}", e.getMessage());
        }
    }

    private void sendNotification(PatientEvent event) {
        log.info("Sending WELCOME notification to patient: {} at email: {}", 
            event.getName(), event.getEmail());
        log.info("Notification content: Welcome {}! Your patient account has been created.", 
            event.getName());
    }
}
