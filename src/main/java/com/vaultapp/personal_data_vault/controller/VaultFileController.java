package com.vaultapp.personal_data_vault.controller;

import com.vaultapp.personal_data_vault.dto.VaultFileResponse;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.web.servlet.function.ServerResponse.ok;

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

    @Operation(
            summary = "Get all files name",
            description = "Get all files name from the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Download uploaded successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Download upload failed", content = @Content)
    })
    @GetMapping("/all")
    public ResponseEntity<List<VaultFileResponse>> getFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }


    @Operation(
            summary = "Download a file",
            description = "Download a file from the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Download uploaded successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Download upload failed", content = @Content)
    })
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        VaultFile vaultFile = fileService.getFileByIdAndUser(id, userDetails.getUsername());

        Path filePath = Paths.get(vaultFile.getStoragePath());

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + vaultFile.getFilename())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(Files.size(filePath))
                    .body(resource);

        } catch (IOException e) {
            logger.error("File not found with ID: {} for user: {}", id, userDetails.getUsername());
            throw new RuntimeException("Error while reading file", e);
        }
    }
    



}
