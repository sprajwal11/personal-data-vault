package com.vaultapp.personal_data_vault.repository;

import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.VaultCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaultCategoryRepository extends JpaRepository<VaultCategory, Long> {

    List<VaultCategory> findByUser(User user);
    
    Optional<VaultCategory> findByIdAndUser(Long id, User user);
}
