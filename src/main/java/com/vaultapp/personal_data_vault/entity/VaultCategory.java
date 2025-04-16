package com.vaultapp.personal_data_vault.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vault_categories")
public class VaultCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. "Personal", "Finance"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VaultCategory(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public VaultCategory() {
    }

    // Getters, setters, constructors
}
