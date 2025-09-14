package com.example.project2.repository;

import com.example.project2.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Поиск активных ролей с пагинацией
    @Query("SELECT r FROM Role r WHERE r.isActive = true AND r.deletedAt IS NULL")
    Page<Role> findActiveRoles(Pageable pageable);
    
    // Поиск всех активных ролей
    @Query("SELECT r FROM Role r WHERE r.isActive = true AND r.deletedAt IS NULL")
    List<Role> findActiveRoles();
    
    // Поиск по названию (только активные)
    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.isActive = true AND r.deletedAt IS NULL")
    Optional<Role> findByName(@Param("name") String name);
    
    // Поиск по коду (только активные)
    @Query("SELECT r FROM Role r WHERE r.code = :code AND r.isActive = true AND r.deletedAt IS NULL")
    Optional<Role> findByCode(@Param("code") String code);
    
    // Поиск системных ролей с пагинацией
    @Query("SELECT r FROM Role r WHERE r.isSystem = true AND r.isActive = true AND r.deletedAt IS NULL")
    Page<Role> findSystemRoles(Pageable pageable);
    
    // Поиск пользовательских ролей с пагинацией
    @Query("SELECT r FROM Role r WHERE r.isSystem = false AND r.isActive = true AND r.deletedAt IS NULL")
    Page<Role> findUserRoles(Pageable pageable);
    
    // Поиск по уровню доступа с пагинацией
    @Query("SELECT r FROM Role r WHERE r.accessLevel = :accessLevel AND r.isActive = true AND r.deletedAt IS NULL")
    Page<Role> findByAccessLevel(@Param("accessLevel") Integer accessLevel, Pageable pageable);
    
    // Поиск ролей с уровнем доступа в диапазоне с пагинацией
    @Query("SELECT r FROM Role r WHERE r.accessLevel BETWEEN :minLevel AND :maxLevel AND r.isActive = true AND r.deletedAt IS NULL")
    Page<Role> findByAccessLevelBetween(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel, Pageable pageable);
    
    // Комплексный поиск с пагинацией
    @Query("SELECT r FROM Role r WHERE " +
           "(:search IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.code) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isSystem IS NULL OR r.isSystem = :isSystem) AND " +
           "(:minAccessLevel IS NULL OR r.accessLevel >= :minAccessLevel) AND " +
           "(:maxAccessLevel IS NULL OR r.accessLevel <= :maxAccessLevel) AND " +
           "r.isActive = true AND r.deletedAt IS NULL")
    Page<Role> findRolesWithFilters(@Param("search") String search,
                                   @Param("isSystem") Boolean isSystem,
                                   @Param("minAccessLevel") Integer minAccessLevel,
                                   @Param("maxAccessLevel") Integer maxAccessLevel,
                                   Pageable pageable);
    
    // Проверка существования роли по названию (только активные)
    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.name = :name AND r.isActive = true AND r.deletedAt IS NULL")
    boolean existsByName(@Param("name") String name);
    
    // Проверка существования роли по коду (только активные)
    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.code = :code AND r.isActive = true AND r.deletedAt IS NULL")
    boolean existsByCode(@Param("code") String code);
    
    // Подсчет количества активных ролей
    @Query("SELECT COUNT(r) FROM Role r WHERE r.isActive = true AND r.deletedAt IS NULL")
    Long countActiveRoles();
    
    // Поиск ролей с максимальным уровнем доступа
    @Query("SELECT r FROM Role r WHERE r.accessLevel = (SELECT MAX(r2.accessLevel) FROM Role r2 WHERE r2.isActive = true AND r2.deletedAt IS NULL) AND r.isActive = true AND r.deletedAt IS NULL")
    List<Role> findRolesWithMaxAccessLevel();
    
    // Поиск ролей с минимальным уровнем доступа
    @Query("SELECT r FROM Role r WHERE r.accessLevel = (SELECT MIN(r2.accessLevel) FROM Role r2 WHERE r2.isActive = true AND r2.deletedAt IS NULL) AND r.isActive = true AND r.deletedAt IS NULL")
    List<Role> findRolesWithMinAccessLevel();
    
    // Логическое удаление
    @Modifying
    @Query("UPDATE Role r SET r.isActive = false, r.deletedAt = :deletedAt WHERE r.id = :id AND r.isSystem = false")
    int softDeleteById(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
    
    // Восстановление из логического удаления
    @Modifying
    @Query("UPDATE Role r SET r.isActive = true, r.deletedAt = NULL WHERE r.id = :id")
    int restoreById(@Param("id") Long id);
    
    // Поиск удаленных ролей
    @Query("SELECT r FROM Role r WHERE r.isActive = false AND r.deletedAt IS NOT NULL")
    Page<Role> findDeletedRoles(Pageable pageable);
}

