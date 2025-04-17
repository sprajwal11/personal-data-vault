package com.vaultapp.personal_data_vault;

import com.vaultapp.personal_data_vault.dto.CreateSecretRequest;
import com.vaultapp.personal_data_vault.dto.VaultSecretResponse;
import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import com.vaultapp.personal_data_vault.entity.VaultSecret;
import com.vaultapp.personal_data_vault.repository.UserRepository;
import com.vaultapp.personal_data_vault.repository.VaultCategoryRepository;
import com.vaultapp.personal_data_vault.repository.VaultSecretRepository;
import com.vaultapp.personal_data_vault.service.VaultSecretService;
import com.vaultapp.personal_data_vault.util.AesEncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VaultSecretServiceTest {

    @Mock
    private VaultSecretRepository secretRepo;

    @Mock
    private VaultCategoryRepository categoryRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private AesEncryptionUtil aesEncryptionUtil;

    @InjectMocks
    private VaultSecretService secretService;

    @Captor
    private ArgumentCaptor<VaultSecret> secretCaptor;

    private User testUser;
    private VaultCategory testCategory;
    private VaultSecret testSecret;
    private CreateSecretRequest createRequest;
    private final String EMAIL = "test@example.com";
    private final String PLAIN_SECRET = "my-secret-password";
    private final String ENCRYPTED_SECRET = "encrypted-value";

    @BeforeEach
    void setup() {
        // Setup test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail(EMAIL);

        // Setup test category
        testCategory = new VaultCategory();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setUser(testUser);

        // Setup test secret
        testSecret = new VaultSecret();
        testSecret.setId(1L);
        testSecret.setLabel("Test Secret");
        testSecret.setEncryptedValue(ENCRYPTED_SECRET);
        testSecret.setCategory(testCategory);
        testSecret.setUser(testUser);

        // Setup create request
        createRequest = new CreateSecretRequest("Test Secret", PLAIN_SECRET, 1L);
    }

    @Test
    void testCreateSecret_Success() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(testCategory));
        when(aesEncryptionUtil.encrypt(PLAIN_SECRET)).thenReturn(ENCRYPTED_SECRET);
        when(secretRepo.save(any(VaultSecret.class))).thenAnswer(invocation -> {
            VaultSecret savedSecret = invocation.getArgument(0);
            savedSecret.setId(1L);
            return savedSecret;
        });

        // Act
        VaultSecretResponse response = secretService.create(createRequest, EMAIL);

        // Assert
        verify(secretRepo).save(secretCaptor.capture());
        VaultSecret capturedSecret = secretCaptor.getValue();

        assertEquals(1L, response.id());
        assertEquals("Test Secret", response.label());
        assertEquals(PLAIN_SECRET, response.secretValue());
        assertEquals(1L, response.categoryId());

        assertEquals("Test Secret", capturedSecret.getLabel());
        assertEquals(ENCRYPTED_SECRET, capturedSecret.getEncryptedValue());
        assertEquals(testCategory, capturedSecret.getCategory());
        assertEquals(testUser, capturedSecret.getUser());
    }

    @Test
    void testCreateSecret_UserNotFound() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                secretService.create(createRequest, EMAIL)
        );

        verify(secretRepo, never()).save(any());
    }

    @Test
    void testCreateSecret_CategoryNotFound() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(categoryRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                secretService.create(createRequest, EMAIL)
        );

        verify(secretRepo, never()).save(any());
    }

    @Test
    void testGetAll_Success() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(secretRepo.findByUser(testUser)).thenReturn(List.of(testSecret));
        when(aesEncryptionUtil.decrypt(ENCRYPTED_SECRET)).thenReturn(PLAIN_SECRET);

        // Act
        List<VaultSecretResponse> responses = secretService.getAll(EMAIL);

        // Assert
        assertEquals(1, responses.size());
        VaultSecretResponse response = responses.get(0);
        assertEquals(1L, response.id());
        assertEquals("Test Secret", response.label());
        assertEquals(PLAIN_SECRET, response.secretValue());
        assertEquals(1L, response.categoryId());
    }

    @Test
    void testGetAll_UserNotFound() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                secretService.getAll(EMAIL)
        );
    }

    @Test
    void testGetAll_EmptyList() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(secretRepo.findByUser(testUser)).thenReturn(List.of());

        // Act
        List<VaultSecretResponse> responses = secretService.getAll(EMAIL);

        // Assert
        assertTrue(responses.isEmpty());
    }

    @Test
    void testDelete_Success() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(secretRepo.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testSecret));

        // Act
        secretService.delete(1L, EMAIL);

        // Assert
        verify(secretRepo).delete(testSecret);
    }

    @Test
    void testDelete_UserNotFound() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                secretService.delete(1L, EMAIL)
        );

        verify(secretRepo, never()).delete(any());
    }

    @Test
    void testDelete_SecretNotFound() {
        // Arrange
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(secretRepo.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                secretService.delete(1L, EMAIL)
        );

        verify(secretRepo, never()).delete(any());
    }
}