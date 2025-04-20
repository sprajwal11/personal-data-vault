package com.vaultapp.personal_data_vault;

import com.vaultapp.personal_data_vault.controller.VaultFileController;
import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import com.vaultapp.personal_data_vault.entity.VaultFile;
import com.vaultapp.personal_data_vault.service.VaultFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VaultFileControllerTest {

    private VaultFileService fileService;
    private VaultFileController controller;

    @BeforeEach
    void setUp() {
        fileService = mock(VaultFileService.class);
        controller = new VaultFileController(fileService);
    }

    @Test
    void testUploadFileSuccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        Long categoryId = 1L;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        VaultFile vaultFile = new VaultFile();
        vaultFile.setId(1L);
        vaultFile.setFilename("test.txt");
        vaultFile.setUser(new User());
        vaultFile.setCategory(new VaultCategory());

        when(fileService.uploadFile(ArgumentMatchers.any(), eq(categoryId), eq("test@example.com"))).thenReturn(vaultFile);

        // When
        ResponseEntity<?> response = controller.uploadFile(file, categoryId, userDetails);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("test.txt"));
        verify(fileService, times(1)).uploadFile(file, categoryId, "test@example.com");
    }

    @Test
    void testUploadFileFailure() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        Long categoryId = 1L;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        when(fileService.uploadFile(ArgumentMatchers.any(), eq(categoryId), eq("test@example.com")))
                .thenThrow(new RuntimeException("Upload failed"));

        // When
        ResponseEntity<?> response = controller.uploadFile(file, categoryId, userDetails);

        // Then
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Upload failed"));
        verify(fileService, times(1)).uploadFile(file, categoryId, "test@example.com");
    }
}
