package com.example.project2.repository;

import com.example.project2.model.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    // Поиск всех профилей с пагинацией
    @Query("SELECT p FROM Profile p WHERE p.deletedAt IS NULL")
    Page<Profile> findAllProfiles(Pageable pageable);
    
    // Поиск всех профилей
    @Query("SELECT p FROM Profile p WHERE p.deletedAt IS NULL")
    List<Profile> findAllProfiles();
    
    // Поиск профиля по пользователю (только активные)
    @Query("SELECT p FROM Profile p WHERE p.user.id = :userId AND p.deletedAt IS NULL")
    Profile findByUserId(@Param("userId") Long userId);
    
    // Поиск по городу с пагинацией
    @Query("SELECT p FROM Profile p WHERE p.city = :city AND p.deletedAt IS NULL")
    Page<Profile> findByCity(@Param("city") String city, Pageable pageable);
    
    // Поиск по стране с пагинацией
    @Query("SELECT p FROM Profile p WHERE p.country = :country AND p.deletedAt IS NULL")
    Page<Profile> findByCountry(@Param("country") String country, Pageable pageable);
    
    // Поиск по городу и стране с пагинацией
    @Query("SELECT p FROM Profile p WHERE p.city = :city AND p.country = :country AND p.deletedAt IS NULL")
    Page<Profile> findByCityAndCountry(@Param("city") String city, @Param("country") String country, Pageable pageable);
    
    // Поиск по почтовому индексу с пагинацией
    @Query("SELECT p FROM Profile p WHERE p.postalCode = :postalCode AND p.deletedAt IS NULL")
    Page<Profile> findByPostalCode(@Param("postalCode") String postalCode, Pageable pageable);
    
    // Комплексный поиск с пагинацией
    @Query("SELECT p FROM Profile p WHERE " +
           "(:search IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.country) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.biography) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:country IS NULL OR LOWER(p.country) LIKE LOWER(CONCAT('%', :country, '%'))) AND " +
           "(:postalCode IS NULL OR p.postalCode = :postalCode) AND " +
           "p.deletedAt IS NULL")
    Page<Profile> findProfilesWithFilters(@Param("search") String search,
                                         @Param("city") String city,
                                         @Param("country") String country,
                                         @Param("postalCode") String postalCode,
                                         Pageable pageable);
    
    // Поиск профилей в определенной стране
    @Query("SELECT p FROM Profile p WHERE p.country = :country AND p.deletedAt IS NULL")
    List<Profile> findProfilesByCountry(@Param("country") String country);
    
    // Поиск профилей в определенном городе
    @Query("SELECT p FROM Profile p WHERE p.city = :city AND p.deletedAt IS NULL")
    List<Profile> findProfilesByCity(@Param("city") String city);
    
    // Проверка существования профиля для пользователя
    @Query("SELECT COUNT(p) > 0 FROM Profile p WHERE p.user.id = :userId AND p.deletedAt IS NULL")
    boolean existsByUserId(@Param("userId") Long userId);
    
    // Логическое удаление
    @Modifying
    @Query("UPDATE Profile p SET p.deletedAt = :deletedAt WHERE p.id = :id")
    int softDeleteById(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
    
    // Восстановление из логического удаления
    @Modifying
    @Query("UPDATE Profile p SET p.deletedAt = NULL WHERE p.id = :id")
    int restoreById(@Param("id") Long id);
    
    // Поиск удаленных профилей
    @Query("SELECT p FROM Profile p WHERE p.deletedAt IS NOT NULL")
    Page<Profile> findDeletedProfiles(Pageable pageable);
}

