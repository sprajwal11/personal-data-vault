package com.vaultapp.personal_data_vault.repository;

import com.vaultapp.personal_data_vault.entity.VaultFile;
import com.vaultapp.personal_data_vault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VaultFileRepository extends JpaRepository<VaultFile, Long> {
    List<VaultFile> findByUser(User user);
    Optional<VaultFile> findByIdAndUser(Long id, User user);
}
