package com.vaultapp.personal_data_vault.controller;

import com.vaultapp.personal_data_vault.dto.CreateCategoryRequest;
import com.vaultapp.personal_data_vault.dto.VaultCategoryResponse;
import com.vaultapp.personal_data_vault.service.VaultCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categories", description = "Manage vault categories")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VaultCategoryController {

    private final VaultCategoryService categoryService;


    @Operation(
            summary = "Create a new category",
            description = "Creates a new category for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VaultCategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VaultCategoryResponse> createCategory(
            @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(categoryService.createCategory(request, user.getUsername()));
    }

    @Operation(
            summary = "Get all categories",
            description = "Returns all categories created by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VaultCategoryResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<VaultCategoryResponse>> getAll(
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(user.getUsername()));
    }

    @Operation(
            summary = "Delete a category",
            description = "Deletes a specific category by ID for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        categoryService.deleteCategory(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
