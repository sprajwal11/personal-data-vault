package com.vaultapp.personal_data_vault;

import com.vaultapp.personal_data_vault.controller.VaultFileController;
import com.vaultapp.personal_data_vault.dto.VaultFileResponse;
import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import com.vaultapp.personal_data_vault.entity.VaultFile;
import com.vaultapp.personal_data_vault.service.VaultFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaultFileControllerTest {

    private VaultFileService fileService;

    @InjectMocks
    private VaultFileController controller;


    @Mock
    private UserDetails userDetails;

    private VaultFile testFile;

    @BeforeEach
    void setUp() {
        fileService = mock(VaultFileService.class);
        controller = new VaultFileController(fileService);

        testFile = VaultFile.builder()
                .id(1L)
                .filename("test.txt")
                .storagePath("src/test/resources/test.txt") // should be a test file
                .uploadedAt(LocalDateTime.now())
                .user(User.builder().email("test@example.com").build())
                .build();

        lenient().when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void testUploadFileSuccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        Long categoryId = 1L;
//        UserDetails userDetails = mock(UserDetails.class);
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
//        UserDetails userDetails = mock(UserDetails.class);
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

    @Test
    void testGetAllFiles() {
        VaultFileResponse response = new VaultFileResponse(1L, "test.txt", "category", LocalDateTime.now());

        when(fileService.getAllFiles()).thenReturn(List.of(response));

        ResponseEntity<List<VaultFileResponse>> result = controller.getFiles();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
        assertEquals("test.txt", result.getBody().get(0).name());
    }

    @Test
    void testDownloadFile_Success() throws Exception {
        Path filePath = Paths.get("src/test/resources/test.txt");
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        when(fileService.getFileByIdAndUser(1L, "test@example.com")).thenReturn(testFile);

        ResponseEntity<Resource> response = controller.downloadFile(1L, userDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getHeaders().get("Content-Disposition").get(0).contains("test.txt"));
        assertNotNull(response.getBody());

        // Clean up
        Files.deleteIfExists(filePath);
    }

    @Test
    void testDownloadFile_NotFound() {
        when(fileService.getFileByIdAndUser(2L, "test@example.com"))
                .thenThrow(new RuntimeException("File not found"));

        assertThrows(RuntimeException.class, () -> {
            controller.downloadFile(2L, userDetails);
        });
    }
}
