package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название проекта не может быть пустым")
    @Size(min = 2, max = 100, message = "Название проекта должно содержать от 2 до 100 символов")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotBlank(message = "Описание проекта не может быть пустым")
    @Size(max = 1000, message = "Описание проекта не должно превышать 1000 символов")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Дата начала обязательна")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Min(value = 0, message = "Бюджет не может быть отрицательным")
    @Column(name = "budget", precision = 15, scale = 2)
    private BigDecimal budget;
    
    @Min(value = 0, message = "Прогресс не может быть отрицательным")
    @Max(value = 100, message = "Прогресс не может превышать 100%")
    @Column(name = "progress")
    private Integer progress = 0;
    
    @NotBlank(message = "Статус проекта не может быть пустым")
    @Pattern(regexp = "^(PLANNING|IN_PROGRESS|COMPLETED|CANCELLED|ON_HOLD)$", 
             message = "Статус должен быть: PLANNING, IN_PROGRESS, COMPLETED, CANCELLED или ON_HOLD")
    @Column(name = "status", nullable = false)
    private String status = "PLANNING";
    
    @NotBlank(message = "Приоритет проекта не может быть пустым")
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH|CRITICAL)$", 
             message = "Приоритет должен быть: LOW, MEDIUM, HIGH или CRITICAL")
    @Column(name = "priority", nullable = false)
    private String priority = "MEDIUM";
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Связь M:M с User
    @ManyToMany(mappedBy = "projects", fetch = FetchType.LAZY)
    private Set<User> users;
    
    // Конструкторы
    public Project() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public Project(String name, String description, LocalDate startDate, String status, String priority) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.status = status;
        this.priority = priority;
        this.progress = 0;
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
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public BigDecimal getBudget() {
        return budget;
    }
    
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
    
    public Integer getProgress() {
        return progress;
    }
    
    public void setProgress(Integer progress) {
        this.progress = progress;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", budget=" + budget +
                ", progress=" + progress +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
