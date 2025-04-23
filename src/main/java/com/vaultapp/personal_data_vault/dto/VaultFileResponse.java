package com.vaultapp.personal_data_vault.dto;

import java.time.LocalDateTime;

public record VaultFileResponse(
    Long id,
    String name,
    String catagory,
    LocalDateTime createdAt
) {

}
