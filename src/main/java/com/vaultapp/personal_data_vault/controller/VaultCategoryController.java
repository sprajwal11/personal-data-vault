package com.vaultapp.personal_data_vault.controller;

import com.vaultapp.personal_data_vault.dto.CreateCategoryRequest;
import com.vaultapp.personal_data_vault.dto.VaultCategoryResponse;
import com.vaultapp.personal_data_vault.service.VaultCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class VaultCategoryController {

    private final VaultCategoryService categoryService;

    @PostMapping
    public ResponseEntity<VaultCategoryResponse> createCategory(
            @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(categoryService.createCategory(request, user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<VaultCategoryResponse>> getAll(
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        categoryService.deleteCategory(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
