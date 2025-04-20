package com.vaultapp.personal_data_vault.service;

import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import com.vaultapp.personal_data_vault.entity.VaultFile;
import com.vaultapp.personal_data_vault.repository.UserRepository;
import com.vaultapp.personal_data_vault.repository.VaultCategoryRepository;
import com.vaultapp.personal_data_vault.repository.VaultFileRepository;
import com.vaultapp.personal_data_vault.util.AesEncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VaultFileService {

    private static final Logger logger = LogManager.getLogger(VaultFileService.class);

    private final VaultFileRepository fileRepo;
    private final VaultCategoryRepository categoryRepo;
    private final UserRepository userRepo;
    private final AesEncryptionUtil aes;

    private final String uploadDir = "uploads/";

    public VaultFile uploadFile(MultipartFile file, Long categoryId, String email) throws IOException {
        logger.info("Uploading file for user: {}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        VaultCategory category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String path = uploadDir + uniqueFileName;

        // Save the file to disk
        File uploadFile = new File(path);
        uploadFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(uploadFile)) {
            fos.write(file.getBytes());
        }

        // Encrypt file path as a mock sensitive data
        String encryptedKey = aes.encrypt(path); // You can enhance this to encrypt actual file content or keys

        VaultFile vaultFile = VaultFile.builder()
                .filename(file.getOriginalFilename())
                .storagePath(path)
                .encryptedKey(encryptedKey)
                .uploadedAt(LocalDateTime.now())
                .category(category)
                .user(user)
                .build();

        return fileRepo.save(vaultFile);
    }
}
