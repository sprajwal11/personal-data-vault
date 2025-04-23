package com.vaultapp.personal_data_vault.dto;

public record VaultSecretResponse(
    Long id,
    String label,
    String secretValue, // decrypted
    Long categoryId
) {}
