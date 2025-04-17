package com.vaultapp.personal_data_vault;

import com.vaultapp.personal_data_vault.dto.CreateCategoryRequest;
import com.vaultapp.personal_data_vault.dto.VaultCategoryResponse;
import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import com.vaultapp.personal_data_vault.repository.UserRepository;
import com.vaultapp.personal_data_vault.repository.VaultCategoryRepository;
import com.vaultapp.personal_data_vault.service.VaultCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaultCategoryServiceTest {

    @Mock
    private VaultCategoryRepository categoryRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private VaultCategoryService categoryService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(UUID.randomUUID()); // ✅ UUID
        user.setEmail("test@example.com");
    }

    @Test
    void testCreateCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest("Finance");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(categoryRepo.save(any())).thenAnswer(invocation -> {
            VaultCategory cat = invocation.getArgument(0);
            cat.setId(100L); // simulate saved
            return cat;
        });

        VaultCategoryResponse response = categoryService.createCategory(request, "test@example.com");

        assertEquals("Finance", response.name());
        assertEquals(100L, response.id());
    }

    @Test
    void testGetAllCategories() {
        VaultCategory cat1 = new VaultCategory();
        cat1.setId(1L);
        cat1.setName("Personal");
        cat1.setUser(user);

        VaultCategory cat2 = new VaultCategory();
        cat2.setId(2L);
        cat2.setName("Work");
        cat2.setUser(user);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(categoryRepo.findByUser(user)).thenReturn(List.of(cat1, cat2));

        List<VaultCategoryResponse> result = categoryService.getAllCategories("test@example.com");

        assertEquals(2, result.size());
        assertEquals("Work", result.get(1).name());
    }

    @Test
    void testDeleteCategory() {
        VaultCategory category = new VaultCategory();
        category.setId(1L);
        category.setUser(user);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(categoryRepo.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L, "test@example.com");

        verify(categoryRepo).delete(category);
    }



}
