package com.example.project2.repository;

import com.example.project2.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    // Поиск активных проектов с пагинацией
    @Query("SELECT p FROM Project p WHERE p.isActive = true AND p.deletedAt IS NULL")
    Page<Project> findActiveProjects(Pageable pageable);
    
    // Поиск всех активных проектов
    @Query("SELECT p FROM Project p WHERE p.isActive = true AND p.deletedAt IS NULL")
    List<Project> findActiveProjects();
    
    // Поиск по названию (только активные)
    @Query("SELECT p FROM Project p WHERE p.name = :name AND p.isActive = true AND p.deletedAt IS NULL")
    Optional<Project> findByName(@Param("name") String name);
    
    // Поиск по статусу с пагинацией
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.isActive = true AND p.deletedAt IS NULL")
    Page<Project> findByStatus(@Param("status") String status, Pageable pageable);
    
    // Поиск по приоритету с пагинацией
    @Query("SELECT p FROM Project p WHERE p.priority = :priority AND p.isActive = true AND p.deletedAt IS NULL")
    Page<Project> findByPriority(@Param("priority") String priority, Pageable pageable);
    
    // Поиск по статусу и приоритету с пагинацией
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.priority = :priority AND p.isActive = true AND p.deletedAt IS NULL")
    Page<Project> findByStatusAndPriority(@Param("status") String status, @Param("priority") String priority, Pageable pageable);
    
    // Поиск проектов в диапазоне дат с пагинацией
    @Query("SELECT p FROM Project p WHERE p.startDate BETWEEN :startDate AND :endDate AND p.isActive = true AND p.deletedAt IS NULL")
    Page<Project> findByStartDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
    
    // Поиск проектов с прогрессом в диапазоне с пагинацией
    @Query("SELECT p FROM Project p WHERE p.progress BETWEEN :minProgress AND :maxProgress AND p.isActive = true AND p.deletedAt IS NULL")
    Page<Project> findByProgressBetween(@Param("minProgress") Integer minProgress, @Param("maxProgress") Integer maxProgress, Pageable pageable);
    
    // Комплексный поиск с пагинацией
    @Query("SELECT p FROM Project p WHERE " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:priority IS NULL OR p.priority = :priority) AND " +
           "(:minProgress IS NULL OR p.progress >= :minProgress) AND " +
           "(:maxProgress IS NULL OR p.progress <= :maxProgress) AND " +
           "(:minBudget IS NULL OR p.budget >= :minBudget) AND " +
           "(:maxBudget IS NULL OR p.budget <= :maxBudget) AND " +
           "(:startDateFrom IS NULL OR p.startDate >= :startDateFrom) AND " +
           "(:startDateTo IS NULL OR p.startDate <= :startDateTo) AND " +
           "p.isActive = true AND p.deletedAt IS NULL")
    Page<Project> findProjectsWithFilters(@Param("search") String search,
                                          @Param("status") String status,
                                          @Param("priority") String priority,
                                          @Param("minProgress") Integer minProgress,
                                          @Param("maxProgress") Integer maxProgress,
                                          @Param("minBudget") BigDecimal minBudget,
                                          @Param("maxBudget") BigDecimal maxBudget,
                                          @Param("startDateFrom") LocalDate startDateFrom,
                                          @Param("startDateTo") LocalDate startDateTo,
                                          Pageable pageable);
    
    // Проверка существования проекта по названию (только активные)
    @Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.name = :name AND p.isActive = true AND p.deletedAt IS NULL")
    boolean existsByName(@Param("name") String name);
    
    // Подсчет количества активных проектов
    @Query("SELECT COUNT(p) FROM Project p WHERE p.isActive = true AND p.deletedAt IS NULL")
    Long countActiveProjects();
    
    // Подсчет проектов по статусу
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status AND p.isActive = true AND p.deletedAt IS NULL")
    Long countProjectsByStatus(@Param("status") String status);
    
    // Поиск проектов с максимальным прогрессом
    @Query("SELECT p FROM Project p WHERE p.progress = (SELECT MAX(p2.progress) FROM Project p2 WHERE p2.isActive = true AND p2.deletedAt IS NULL) AND p.isActive = true AND p.deletedAt IS NULL")
    List<Project> findProjectsWithMaxProgress();
    
    // Поиск проектов с минимальным прогрессом
    @Query("SELECT p FROM Project p WHERE p.progress = (SELECT MIN(p2.progress) FROM Project p2 WHERE p2.isActive = true AND p2.deletedAt IS NULL) AND p.isActive = true AND p.deletedAt IS NULL")
    List<Project> findProjectsWithMinProgress();
    
    // Поиск просроченных проектов
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate AND p.status != 'COMPLETED' AND p.isActive = true AND p.deletedAt IS NULL")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDate currentDate);
    
    // Поиск проектов, которые должны завершиться в ближайшие дни
    @Query("SELECT p FROM Project p WHERE p.endDate BETWEEN :startDate AND :endDate AND p.isActive = true AND p.deletedAt IS NULL")
    List<Project> findProjectsEndingSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Логическое удаление
    @Modifying
    @Query("UPDATE Project p SET p.isActive = false, p.deletedAt = :deletedAt WHERE p.id = :id")
    int softDeleteById(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
    
    // Восстановление из логического удаления
    @Modifying
    @Query("UPDATE Project p SET p.isActive = true, p.deletedAt = NULL WHERE p.id = :id")
    int restoreById(@Param("id") Long id);
    
    // Поиск удаленных проектов
    @Query("SELECT p FROM Project p WHERE p.isActive = false AND p.deletedAt IS NOT NULL")
    Page<Project> findDeletedProjects(Pageable pageable);
}

