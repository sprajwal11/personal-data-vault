package com.vaultapp.personal_data_vault.controller;

import com.vaultapp.personal_data_vault.entity.VaultFile;
import com.vaultapp.personal_data_vault.service.VaultFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Files", description = "Manage vault files")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VaultFileController {

    private static final Logger logger = LogManager.getLogger(VaultFileController.class);

    private final VaultFileService fileService;

    @Operation(
            summary = "Upload a file",
            description = "Uploads a file to a specific category for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "File upload failed", content = @Content)
    })
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
