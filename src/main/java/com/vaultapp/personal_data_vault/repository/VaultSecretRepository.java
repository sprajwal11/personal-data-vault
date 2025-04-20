package com.vaultapp.personal_data_vault.repository;

import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultSecret;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VaultSecretRepository extends JpaRepository<VaultSecret, Long> {
    List<VaultSecret> findByUser(User user);
    Optional<VaultSecret> findByIdAndUser(Long id, User user);

    List<VaultSecret> findByUserId(UUID id);

    Page<VaultSecret> findByUserAndLabelContainingIgnoreCase(User user, String label, Pageable pageable);

}
