package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название роли не может быть пустым")
    @Size(min = 2, max = 50, message = "Название роли должно содержать от 2 до 50 символов")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @NotBlank(message = "Описание роли не может быть пустым")
    @Size(max = 200, message = "Описание роли не должно превышать 200 символов")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "Код роли не может быть пустым")
    @Size(min = 2, max = 20, message = "Код роли должен содержать от 2 до 20 символов")
    @Pattern(regexp = "^[A-Z_]+$", message = "Код роли должен содержать только заглавные буквы и подчеркивания")
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Min(value = 1, message = "Уровень доступа должен быть положительным")
    @Max(value = 10, message = "Уровень доступа не должен превышать 10")
    @Column(name = "access_level", nullable = false)
    private Integer accessLevel;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Связь M:M с User
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users;
    
    // Конструкторы
    public Role() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.isSystem = false;
    }
    
    public Role(String name, String description, String code, Integer accessLevel) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.accessLevel = accessLevel;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.isSystem = false;
    }
    
    // Геттеры и сеттеры
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public Integer getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsSystem() {
        return isSystem;
    }
    
    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }
    
    public Set<User> getUsers() {
        return users;
    }
    
    public void setUsers(Set<User> users) {
        this.users = users;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", accessLevel=" + accessLevel +
                ", isActive=" + isActive +
                ", isSystem=" + isSystem +
                '}';
    }
}
