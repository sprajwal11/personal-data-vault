package com.vaultapp.personal_data_vault.service;

import com.vaultapp.personal_data_vault.dto.CreateCategoryRequest;
import com.vaultapp.personal_data_vault.dto.VaultCategoryResponse;
import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import com.vaultapp.personal_data_vault.repository.UserRepository;
import com.vaultapp.personal_data_vault.repository.VaultCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaultCategoryService {

    private final VaultCategoryRepository categoryRepo;
    private final UserRepository userRepo;

    public VaultCategoryResponse createCategory(CreateCategoryRequest request, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        VaultCategory category = new VaultCategory();
        category.setName(request.name());
        category.setUser(user);

        categoryRepo.save(category);
        return new VaultCategoryResponse(category.getId(), category.getName());
    }

    public List<VaultCategoryResponse> getAllCategories(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return categoryRepo.findByUser(user)
                .stream()
                .map(cat -> new VaultCategoryResponse(cat.getId(), cat.getName()))
                .toList();
    }

    //unused right now
    public VaultCategoryResponse getCategory(Long id) {
        VaultCategory vaultCategory = categoryRepo.findById(id).orElseThrow(()->new EntityNotFoundException("Category not found"));
            return new VaultCategoryResponse(vaultCategory.getId(), vaultCategory.getName());
    }

    public void deleteCategory(Long categoryId, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        VaultCategory category = categoryRepo.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        categoryRepo.delete(category);
    }
}
