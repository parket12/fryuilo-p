package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название отдела не может быть пустым")
    @Size(min = 2, max = 100, message = "Название отдела должно содержать от 2 до 100 символов")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "Код отдела не может быть пустым")
    @Size(min = 2, max = 10, message = "Код отдела должен содержать от 2 до 10 символов")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Код отдела должен содержать только заглавные буквы и цифры")
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Min(value = 1, message = "Бюджет должен быть положительным")
    @Column(name = "budget")
    private Double budget;
    
    @Min(value = 1, message = "Количество сотрудников должно быть положительным")
    @Max(value = 1000, message = "Количество сотрудников не должно превышать 1000")
    @Column(name = "employee_count")
    private Integer employeeCount;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Связь 1:M с User
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;
    
    // Конструкторы
    public Department() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public Department(String name, String description, String code) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
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
    
    public Double getBudget() {
        return budget;
    }
    
    public void setBudget(Double budget) {
        this.budget = budget;
    }
    
    public Integer getEmployeeCount() {
        return employeeCount;
    }
    
    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<User> getUsers() {
        return users;
    }
    
    public void setUsers(List<User> users) {
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
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", budget=" + budget +
                ", employeeCount=" + employeeCount +
                ", isActive=" + isActive +
                '}';
    }
}
