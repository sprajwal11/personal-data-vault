package com.vaultapp.personal_data_vault.controller;

import com.vaultapp.personal_data_vault.dto.CreateSecretRequest;
import com.vaultapp.personal_data_vault.dto.VaultSecretResponse;
import com.vaultapp.personal_data_vault.security.JwtService;
import com.vaultapp.personal_data_vault.service.VaultSecretService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Secrets", description = "Manage vault secrets")
@RestController
@RequestMapping("/api/secrets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VaultSecretController {

    private final VaultSecretService secretService;
    private final JwtService jwtService;

    @Operation(summary = "1, Create a secret", description = "Creates a new secret for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Secret created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VaultSecretResponse> createSecret(
            @RequestBody @Valid CreateSecretRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
//        System.out.println("====================");
//        System.out.println("hello");
//        System.out.println(request.label());
        return ResponseEntity.ok(secretService.create(request, user.getUsername()));
    }

    @Operation(summary = "2, Get all secrets", description = "Retrieves all secrets associated with the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Secrets retrieved successfully")
    @GetMapping
    public ResponseEntity<List<VaultSecretResponse>> getAll(
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(secretService.getAll(user.getUsername()));
    }


    @Operation(summary = "3. Delete a secret", description = "Deletes a specific secret owned by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Secret deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Secret not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecret(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        secretService.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "4. Search secrets", description = "Searches the user's secrets based on a keyword.")
    @ApiResponse(responseCode = "200", description = "Secrets found")
    @GetMapping("/search")
    public Page<VaultSecretResponse> searchSecrets(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "label") String sortBy,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtService.extractUsername(token.substring(7)); // remove "Bearer "
        return secretService.searchSecrets(email, query, page, size, sortBy);
    }
}
