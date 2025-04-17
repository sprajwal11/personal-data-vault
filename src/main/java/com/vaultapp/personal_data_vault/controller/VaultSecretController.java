package com.vaultapp.personal_data_vault.controller;

import com.vaultapp.personal_data_vault.dto.CreateSecretRequest;
import com.vaultapp.personal_data_vault.dto.VaultSecretResponse;
import com.vaultapp.personal_data_vault.service.VaultSecretService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secrets")
@RequiredArgsConstructor
public class VaultSecretController {

    private final VaultSecretService secretService;

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

    @GetMapping
    @Transactional
    public ResponseEntity<List<VaultSecretResponse>> getAll(
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(secretService.getAll(user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecret(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        secretService.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
