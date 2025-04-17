package com.vaultapp.personal_data_vault.dto;

public record CreateSecretRequest(
    String label,
    String secretValue,
    Long categoryId
) {}
