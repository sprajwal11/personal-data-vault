package com.vaultapp.personal_data_vault;

import com.vaultapp.personal_data_vault.entity.AuditLog;
import com.vaultapp.personal_data_vault.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class AuditLogAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void shouldLogAuditEntryWhenProtectedEndpointIsAccessed() throws Exception {
        // Call any endpoint that is annotated with @LogAudit
        mockMvc.perform(get("/api/v1/health"))  // replace with any annotated endpoint
                .andReturn();

        // Fetch logs
        List<AuditLog> logs = auditLogRepository.findAll();

        // Assert
        assertThat(logs).isNotEmpty();
        AuditLog log = logs.get(logs.size() - 1);  // Get the latest
        assertThat(log.getAction()).isNotBlank();
        assertThat(log.getIpAddress()).isNotBlank();
        assertThat(log.getUserAgent()).isNotBlank();
        assertThat(log.getTimestamp()).isNotNull();
    }
}
