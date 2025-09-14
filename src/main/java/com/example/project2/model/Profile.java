package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
public class Profile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Адрес не может быть пустым")
    @Size(max = 200, message = "Адрес не должен превышать 200 символов")
    @Column(name = "address", nullable = false)
    private String address;
    
    @NotBlank(message = "Город не может быть пустым")
    @Size(max = 50, message = "Город не должен превышать 50 символов")
    @Column(name = "city", nullable = false)
    private String city;
    
    @NotBlank(message = "Страна не может быть пустой")
    @Size(max = 50, message = "Страна не должна превышать 50 символов")
    @Column(name = "country", nullable = false)
    private String country;
    
    @Size(max = 10, message = "Почтовый индекс не должен превышать 10 символов")
    @Pattern(regexp = "^\\d{5,10}$", message = "Почтовый индекс должен содержать только цифры")
    @Column(name = "postal_code")
    private String postalCode;
    
    @Size(max = 500, message = "Биография не должна превышать 500 символов")
    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Связь 1:1 с User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Конструкторы
    public Profile() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Profile(String address, String city, String country, User user) {
        this.address = address;
        this.city = city;
        this.country = country;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
    
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getBiography() {
        return biography;
    }
    
    public void setBiography(String biography) {
        this.biography = biography;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
        return "Profile{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
