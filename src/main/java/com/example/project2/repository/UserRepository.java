package com.example.project2.repository;

import com.example.project2.model.User;
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
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Поиск активных пользователей с пагинацией
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL")
    Page<User> findActiveUsers(Pageable pageable);
    
    // Поиск всех активных пользователей
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL")
    List<User> findActiveUsers();
    
    // Поиск по email (только активные)
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true AND u.deletedAt IS NULL")
    Optional<User> findByEmail(@Param("email") String email);
    
    // Поиск по телефону (только активные)
    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.isActive = true AND u.deletedAt IS NULL")
    Optional<User> findByPhone(@Param("phone") String phone);
    
    // Поиск по имени и фамилии с пагинацией
    @Query("SELECT u FROM User u WHERE " +
           "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "u.isActive = true AND u.deletedAt IS NULL")
    Page<User> findByFirstNameAndLastName(@Param("firstName") String firstName, 
                                         @Param("lastName") String lastName, 
                                         Pageable pageable);
    
    // Поиск по отделу с пагинацией
    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId AND u.isActive = true AND u.deletedAt IS NULL")
    Page<User> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);
    
    // Поиск пользователей с определенной ролью с пагинацией
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.isActive = true AND u.deletedAt IS NULL")
    Page<User> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);
    
    // Поиск пользователей в определенном проекте с пагинацией
    @Query("SELECT u FROM User u JOIN u.projects p WHERE p.id = :projectId AND u.isActive = true AND u.deletedAt IS NULL")
    Page<User> findByProjectId(@Param("projectId") Long projectId, Pageable pageable);
    
    // Поиск по возрасту с пагинацией
    @Query("SELECT u FROM User u WHERE u.age BETWEEN :minAge AND :maxAge AND u.isActive = true AND u.deletedAt IS NULL")
    Page<User> findByAgeBetween(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge, Pageable pageable);
    
    // Комплексный поиск с пагинацией
    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:departmentId IS NULL OR u.department.id = :departmentId) AND " +
           "(:roleId IS NULL OR EXISTS (SELECT 1 FROM u.roles r WHERE r.id = :roleId)) AND " +
           "(:projectId IS NULL OR EXISTS (SELECT 1 FROM u.projects p WHERE p.id = :projectId)) AND " +
           "(:minAge IS NULL OR u.age >= :minAge) AND " +
           "(:maxAge IS NULL OR u.age <= :maxAge) AND " +
           "u.isActive = true AND u.deletedAt IS NULL")
    Page<User> findUsersWithFilters(@Param("search") String search,
                                   @Param("departmentId") Long departmentId,
                                   @Param("roleId") Long roleId,
                                   @Param("projectId") Long projectId,
                                   @Param("minAge") Integer minAge,
                                   @Param("maxAge") Integer maxAge,
                                   Pageable pageable);
    
    // Проверка существования email (только активные)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.isActive = true AND u.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);
    
    // Проверка существования телефона (только активные)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone AND u.isActive = true AND u.deletedAt IS NULL")
    boolean existsByPhone(@Param("phone") String phone);
    
    // Логическое удаление
    @Modifying
    @Query("UPDATE User u SET u.isActive = false, u.deletedAt = :deletedAt WHERE u.id = :id")
    int softDeleteById(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
    
    // Восстановление из логического удаления
    @Modifying
    @Query("UPDATE User u SET u.isActive = true, u.deletedAt = NULL WHERE u.id = :id")
    int restoreById(@Param("id") Long id);
    
    // Поиск удаленных пользователей
    @Query("SELECT u FROM User u WHERE u.isActive = false AND u.deletedAt IS NOT NULL")
    Page<User> findDeletedUsers(Pageable pageable);
}

