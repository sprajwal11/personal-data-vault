package com.vaultapp.personal_data_vault.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaultFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String storagePath;

    private String encryptedKey;

    @ManyToOne
    private User user;

    @ManyToOne
    private VaultCategory category;

    private LocalDateTime uploadedAt;

}
