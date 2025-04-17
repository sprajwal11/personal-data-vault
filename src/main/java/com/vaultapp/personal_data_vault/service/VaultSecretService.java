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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaultSecretService {

    private final VaultSecretRepository secretRepo;
    private final VaultCategoryRepository categoryRepo;
    private final UserRepository userRepo;
    private final AesEncryptionUtil aes;

    public VaultSecretResponse create(CreateSecretRequest req, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//        System.out.println("================");
//        System.out.println(user.getEmail());

        VaultCategory category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

//        System.out.println("================");
//        System.out.println("categary id: "+req.categoryId());

        VaultSecret secret = new VaultSecret();
        secret.setLabel(req.label());
        secret.setEncryptedValue(aes.encrypt(req.secretValue()));
        secret.setCategory(category);
        secret.setUser(user);

        secretRepo.save(secret);

        return new VaultSecretResponse(
                secret.getId(),
                secret.getLabel(),
                req.secretValue(),
                category.getId()
        );
    }

    public List<VaultSecretResponse> getAll(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<VaultSecretResponse> list = secretRepo.findByUser(user).stream()
                .map(secret -> new VaultSecretResponse(
                        secret.getId(),
                        secret.getLabel(),
                        aes.decrypt(secret.getEncryptedValue()),
                        secret.getCategory().getId()
                ))
                .toList();
        return list;
    }

    public void delete(Long id, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        VaultSecret secret = secretRepo.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Secret not found"));

        secretRepo.delete(secret);
    }
}
