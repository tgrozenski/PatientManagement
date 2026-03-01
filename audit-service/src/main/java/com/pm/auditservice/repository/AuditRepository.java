package com.pm.auditservice.repository;

import com.pm.auditservice.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<AuditEvent, UUID> {
}
