package com.vaultapp.personal_data_vault.service;

import com.vaultapp.personal_data_vault.dto.CreateSecretRequest;
import com.vaultapp.personal_data_vault.dto.VaultSecretResponse;
import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import com.vaultapp.personal_data_vault.entity.VaultSecret;
import com.vaultapp.personal_data_vault.repository.UserRepository;
import com.vaultapp.personal_data_vault.repository.VaultCategoryRepository;
import com.vaultapp.personal_data_vault.repository.VaultSecretRepository;
import com.vaultapp.personal_data_vault.util.AesEncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultSecretService {

    private final VaultSecretRepository secretRepo;
    private final VaultCategoryRepository categoryRepo;
    private final UserRepository userRepo;
    private final AesEncryptionUtil aes;

    public VaultSecretResponse create(CreateSecretRequest req, String email) {
        log.info("Creating a secret for user email: {}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        VaultCategory category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with ID: {}", req.categoryId());
                    return new EntityNotFoundException("Category not found");
                });

        VaultSecret secret = new VaultSecret();
        secret.setLabel(req.label());
        secret.setEncryptedValue(aes.encrypt(req.secretValue()));
        secret.setCategory(category);
        secret.setUser(user);

        VaultSecret savedSecret = secretRepo.save(secret);

        log.info("Secret created with ID: {} for user: {}", savedSecret.getId(), user.getEmail());

        return new VaultSecretResponse(
                savedSecret.getId(),
                savedSecret.getLabel(),
                req.secretValue(),
                category.getId()
        );
    }

    public List<VaultSecretResponse> getAll(String email) {
        log.info("Fetching all secrets for user email: {}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        List<VaultSecretResponse> list = secretRepo.findByUser(user).stream()
                .map(secret -> new VaultSecretResponse(
                        secret.getId(),
                        secret.getLabel(),
                        aes.decrypt(secret.getEncryptedValue()),
                        secret.getCategory().getId()
                ))
                .toList();

        log.info("Fetched {} secrets for user: {}", list.size(), email);
        return list;
    }

    public void delete(Long id, String email) {
        log.info("Attempting to delete secret with ID: {} for user: {}", id, email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        VaultSecret secret = secretRepo.findByIdAndUser(id, user)
                .orElseThrow(() -> {
                    log.error("Secret not found with ID: {} for user: {}", id, email);
                    return new EntityNotFoundException("Secret not found");
                });

        secretRepo.delete(secret);
        log.info("Deleted secret with ID: {} for user: {}", id, email);
    }

    public VaultSecret createSecret(User user, CreateSecretRequest request) {
        log.info("Creating secret for user ID: {}", user.getId());

        VaultCategory category = categoryRepo.findByIdAndUserId(request.categoryId(), user.getId())
                .orElseThrow(() -> {
                    log.error("Category not found with ID: {} for user ID: {}", request.categoryId(), user.getId());
                    return new RuntimeException("Category not found");
                });

        VaultSecret secret = new VaultSecret();
        secret.setLabel(request.label());
        secret.setEncryptedValue(request.secretValue());
        secret.setTitle(request.label()); // Optional
        secret.setCategory(category);
        secret.setUser(user);

        VaultSecret savedSecret = secretRepo.save(secret);
        log.info("Secret created with ID: {} for user ID: {}", savedSecret.getId(), user.getId());
        return savedSecret;
    }

    public List<VaultSecret> getSecretsByUser(User user) {
        log.info("Retrieving secrets for user ID: {}", user.getId());
        return secretRepo.findByUserId(user.getId());
    }
}
