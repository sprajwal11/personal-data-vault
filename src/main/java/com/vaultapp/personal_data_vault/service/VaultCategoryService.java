package com.vaultapp.personal_data_vault.service;

import com.vaultapp.personal_data_vault.dto.CreateCategoryRequest;
import com.vaultapp.personal_data_vault.dto.VaultCategoryResponse;
import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import com.vaultapp.personal_data_vault.repository.UserRepository;
import com.vaultapp.personal_data_vault.repository.VaultCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaultCategoryService {

    private static final Logger logger = LogManager.getLogger(VaultCategoryService.class);

    private final VaultCategoryRepository categoryRepo;
    private final UserRepository userRepo;

    public VaultCategoryResponse createCategory(CreateCategoryRequest request, String email) {
        logger.info("Creating a new category for user email: {}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        VaultCategory category = new VaultCategory();
        category.setName(request.name());
        category.setUser(user);

        VaultCategory savedCategory = categoryRepo.save(category);
        logger.info("Category created with ID: {} and name: {}", savedCategory.getId(), savedCategory.getName());

        return new VaultCategoryResponse(savedCategory.getId(), savedCategory.getName());
    }

    public List<VaultCategoryResponse> getAllCategories(String email) {
        logger.info("Fetching all categories for user email: {}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        List<VaultCategoryResponse> list = categoryRepo.findByUser(user)
                .stream()
                .map(cat -> new VaultCategoryResponse(cat.getId(), cat.getName()))
                .toList();

        logger.info("Fetched {} categories for user: {}", list.size(), email);
        return list;
    }

    public VaultCategoryResponse getCategory(Long id) {
        logger.info("Fetching category with ID: {}", id);

        VaultCategory vaultCategory = categoryRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: {}", id);
                    return new EntityNotFoundException("Category not found");
                });

        return new VaultCategoryResponse(vaultCategory.getId(), vaultCategory.getName());
    }

    public void deleteCategory(Long categoryId, String email) {
        logger.info("Deleting category with ID: {} for user email: {}", categoryId, email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        VaultCategory category = categoryRepo.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: {} for user: {}", categoryId, email);
                    return new EntityNotFoundException("Category not found");
                });

        categoryRepo.delete(category);
        logger.info("Deleted category with ID: {} for user: {}", categoryId, email);
    }
}
