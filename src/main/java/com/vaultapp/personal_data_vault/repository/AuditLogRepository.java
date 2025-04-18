package com.vaultapp.personal_data_vault.repository;

import com.vaultapp.personal_data_vault.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
