package com.pm.auditservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.auditservice.model.AuditEvent;
import com.pm.auditservice.repository.AuditRepository;
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
    private final AuditRepository auditRepository;

    public KafkaConsumer(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @RetryableTopic(
        attempts = "4",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "patient", groupId = "audit-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            saveAuditRecord(patientEvent);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event: {}", e.getMessage());
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    @KafkaListener(topics = "patient-dlt", groupId = "audit-service")
    public void handleDlt(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.error("DLQ: Could not save audit record for patient: {}",
                patientEvent.getPatientId());
        } catch (InvalidProtocolBufferException e) {
            log.error("Could not deserialize DLQ message: {}", e.getMessage());
        }
    }

    private void saveAuditRecord(PatientEvent event) {
        AuditEvent auditEvent = new AuditEvent(
            event.getPatientId(),
            event.getName(),
            event.getEmail(),
            event.getEventType()
        );
        auditRepository.save(auditEvent);
        log.info("Audit record saved for patient: {} with event type: {}", 
            event.getPatientId(), event.getEventType());
    }
}
