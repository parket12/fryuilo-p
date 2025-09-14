package com.example.project2.repository;

import com.example.project2.model.Department;
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
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // Поиск активных отделов с пагинацией
    @Query("SELECT d FROM Department d WHERE d.isActive = true AND d.deletedAt IS NULL")
    Page<Department> findActiveDepartments(Pageable pageable);
    
    // Поиск всех активных отделов
    @Query("SELECT d FROM Department d WHERE d.isActive = true AND d.deletedAt IS NULL")
    List<Department> findActiveDepartments();
    
    // Поиск по названию (только активные)
    @Query("SELECT d FROM Department d WHERE d.name = :name AND d.isActive = true AND d.deletedAt IS NULL")
    Optional<Department> findByName(@Param("name") String name);
    
    // Поиск по коду (только активные)
    @Query("SELECT d FROM Department d WHERE d.code = :code AND d.isActive = true AND d.deletedAt IS NULL")
    Optional<Department> findByCode(@Param("code") String code);
    
    // Поиск по бюджету с пагинацией
    @Query("SELECT d FROM Department d WHERE d.budget > :budget AND d.isActive = true AND d.deletedAt IS NULL")
    Page<Department> findByBudgetGreaterThan(@Param("budget") Double budget, Pageable pageable);
    
    // Поиск по количеству сотрудников с пагинацией
    @Query("SELECT d FROM Department d WHERE d.employeeCount > :employeeCount AND d.isActive = true AND d.deletedAt IS NULL")
    Page<Department> findByEmployeeCountGreaterThan(@Param("employeeCount") Integer employeeCount, Pageable pageable);
    
    // Поиск отделов с бюджетом в диапазоне с пагинацией
    @Query("SELECT d FROM Department d WHERE d.budget BETWEEN :minBudget AND :maxBudget AND d.isActive = true AND d.deletedAt IS NULL")
    Page<Department> findByBudgetBetween(@Param("minBudget") Double minBudget, @Param("maxBudget") Double maxBudget, Pageable pageable);
    
    // Поиск отделов с количеством сотрудников в диапазоне с пагинацией
    @Query("SELECT d FROM Department d WHERE d.employeeCount BETWEEN :minCount AND :maxCount AND d.isActive = true AND d.deletedAt IS NULL")
    Page<Department> findByEmployeeCountBetween(@Param("minCount") Integer minCount, @Param("maxCount") Integer maxCount, Pageable pageable);
    
    @Query("SELECT d FROM Department d WHERE " +
           "(:search IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:minBudget IS NULL OR d.budget >= :minBudget) AND " +
           "(:maxBudget IS NULL OR d.budget <= :maxBudget) AND " +
           "(:minEmployeeCount IS NULL OR d.employeeCount >= :minEmployeeCount) AND " +
           "(:maxEmployeeCount IS NULL OR d.employeeCount <= :maxEmployeeCount) AND " +
           "d.isActive = true AND d.deletedAt IS NULL")
    Page<Department> findDepartmentsWithFilters(@Param("search") String search,
                                              @Param("minBudget") Double minBudget,
                                              @Param("maxBudget") Double maxBudget,
                                              @Param("minEmployeeCount") Integer minEmployeeCount,
                                              @Param("maxEmployeeCount") Integer maxEmployeeCount,
                                              Pageable pageable);
    
    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.name = :name AND d.isActive = true AND d.deletedAt IS NULL")
    boolean existsByName(@Param("name") String name);
    
    // Проверка существования отдела по коду (только активные)
    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.code = :code AND d.isActive = true AND d.deletedAt IS NULL")
    boolean existsByCode(@Param("code") String code);
    
    // Подсчет количества активных отделов
    @Query("SELECT COUNT(d) FROM Department d WHERE d.isActive = true AND d.deletedAt IS NULL")
    Long countActiveDepartments();
    
    // Подсчет общего количества сотрудников во всех отделах
    @Query("SELECT SUM(d.employeeCount) FROM Department d WHERE d.isActive = true AND d.deletedAt IS NULL")
    Long sumActiveEmployeeCount();
    
    // Логическое удаление
    @Modifying
    @Query("UPDATE Department d SET d.isActive = false, d.deletedAt = :deletedAt WHERE d.id = :id")
    int softDeleteById(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
    
    // Восстановление из логического удаления
    @Modifying
    @Query("UPDATE Department d SET d.isActive = true, d.deletedAt = NULL WHERE d.id = :id")
    int restoreById(@Param("id") Long id);
    
    // Поиск удаленных отделов
    @Query("SELECT d FROM Department d WHERE d.isActive = false AND d.deletedAt IS NOT NULL")
    Page<Department> findDeletedDepartments(Pageable pageable);
}

