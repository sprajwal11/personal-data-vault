package com.vaultapp.personal_data_vault.controller;

import com.vaultapp.personal_data_vault.entity.VaultFile;
import com.vaultapp.personal_data_vault.service.VaultFileService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class VaultFileController {

    private static final Logger logger = LogManager.getLogger(VaultFileController.class);

    private final VaultFileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("categoryId") Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            VaultFile uploaded = fileService.uploadFile(file, categoryId, userDetails.getUsername());
            logger.info("File uploaded successfully: {}", uploaded.getFilename());
            return ResponseEntity.ok("File uploaded successfully: " + uploaded.getFilename());
        } catch (Exception e) {
            logger.error("Failed to upload file", e);
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }
}
