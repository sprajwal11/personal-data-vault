package com.vaultapp.personal_data_vault.aspect;

import com.vaultapp.personal_data_vault.annotation.LogAudit;
import com.vaultapp.personal_data_vault.entity.AuditLog;
import com.vaultapp.personal_data_vault.repository.AuditLogRepository;
import com.vaultapp.personal_data_vault.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;

    @Around("@annotation(logAudit)")
    public Object logAuditAction(ProceedingJoinPoint joinPoint, LogAudit logAudit) throws Throwable {
        AuditLog auditLog = new AuditLog();

        // Set static audit details
        auditLog.setAction(logAudit.action());
        auditLog.setIpAddress(request.getRemoteAddr());
        auditLog.setUserAgent(request.getHeader("User-Agent"));
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setHttpMethod(request.getMethod());

        // Get authenticated user from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                UUID userId = userDetails.getId();
                auditLog.setUserId(userId);
            }
        }

        try {
            Object result = joinPoint.proceed();
            auditLog.setStatus("SUCCESS");
            return result;
        } catch (Exception ex) {
            auditLog.setStatus("ERROR");
            auditLog.setErrorMessage(ex.getMessage());
            log.error("Error during audited action: {}", ex.getMessage(), ex);
            throw ex; // Re-throw the exception so original logic behaves as expected
        } finally {
            auditLogRepository.save(auditLog);
        }
    }
}