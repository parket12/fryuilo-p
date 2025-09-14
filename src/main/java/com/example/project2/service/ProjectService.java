package com.example.project2.service;

import com.example.project2.model.Project;
import com.example.project2.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    // Создать проект
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }
    
    // Получить все проекты с пагинацией
    @Transactional(readOnly = true)
    public Page<Project> getAllProjects(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findActiveProjects(pageable);
    }
    
    // Получить все проекты
    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    // Получить активные проекты
    @Transactional(readOnly = true)
    public List<Project> getActiveProjects() {
        return projectRepository.findActiveProjects();
    }
    
    // Получить проект по ID
    @Transactional(readOnly = true)
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
    
    // Получить проект по названию
    @Transactional(readOnly = true)
    public Optional<Project> getProjectByName(String name) {
        return projectRepository.findByName(name);
    }
    
    // Обновить проект
    public Project updateProject(Long id, Project projectDetails) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            project.setName(projectDetails.getName());
            project.setDescription(projectDetails.getDescription());
            project.setStartDate(projectDetails.getStartDate());
            project.setEndDate(projectDetails.getEndDate());
            project.setBudget(projectDetails.getBudget());
            project.setProgress(projectDetails.getProgress());
            project.setStatus(projectDetails.getStatus());
            project.setPriority(projectDetails.getPriority());
            project.setIsActive(projectDetails.getIsActive());
            return projectRepository.save(project);
        }
        return null;
    }
    
    // Физическое удаление проекта
    public boolean deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Логическое удаление проекта
    public boolean softDeleteProject(Long id) {
        int result = projectRepository.softDeleteById(id, LocalDateTime.now());
        return result > 0;
    }
    
    // Восстановление проекта из логического удаления
    public boolean restoreProject(Long id) {
        int result = projectRepository.restoreById(id);
        return result > 0;
    }
    
    // Поиск с фильтрами и пагинацией
    @Transactional(readOnly = true)
    public Page<Project> searchProjects(String search, String status, String priority, 
                                      Integer minProgress, Integer maxProgress,
                                      BigDecimal minBudget, BigDecimal maxBudget,
                                      LocalDate startDateFrom, LocalDate startDateTo,
                                      int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return projectRepository.findProjectsWithFilters(search, status, priority, 
                                                        minProgress, maxProgress,
                                                        minBudget, maxBudget,
                                                        startDateFrom, startDateTo, pageable);
    }
    
    // Поиск по статусу с пагинацией
    @Transactional(readOnly = true)
    public Page<Project> findByStatus(String status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findByStatus(status, pageable);
    }
    
    // Поиск по приоритету с пагинацией
    @Transactional(readOnly = true)
    public Page<Project> findByPriority(String priority, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findByPriority(priority, pageable);
    }
    
    // Поиск по статусу и приоритету с пагинацией
    @Transactional(readOnly = true)
    public Page<Project> findByStatusAndPriority(String status, String priority, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findByStatusAndPriority(status, priority, pageable);
    }
    
    // Поиск проектов в диапазоне дат с пагинацией
    @Transactional(readOnly = true)
    public Page<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findByStartDateBetween(startDate, endDate, pageable);
    }
    
    // Поиск проектов с прогрессом в диапазоне с пагинацией
    @Transactional(readOnly = true)
    public Page<Project> findByProgressBetween(Integer minProgress, Integer maxProgress, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findByProgressBetween(minProgress, maxProgress, pageable);
    }
    
    // Поиск удаленных проектов
    @Transactional(readOnly = true)
    public Page<Project> findDeletedProjects(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findDeletedProjects(pageable);
    }
    
    // Проверка существования проекта по названию
    @Transactional(readOnly = true)
    public boolean projectNameExists(String name) {
        return projectRepository.existsByName(name);
    }
    
    // Проверка существования проекта
    @Transactional(readOnly = true)
    public boolean projectExists(Long id) {
        return projectRepository.existsById(id);
    }
    
    // Подсчет количества активных проектов
    @Transactional(readOnly = true)
    public Long countActiveProjects() {
        return projectRepository.countActiveProjects();
    }
    
    // Подсчет проектов по статусу
    @Transactional(readOnly = true)
    public Long countProjectsByStatus(String status) {
        return projectRepository.countProjectsByStatus(status);
    }
    
    // Поиск проектов с максимальным прогрессом
    @Transactional(readOnly = true)
    public List<Project> findProjectsWithMaxProgress() {
        return projectRepository.findProjectsWithMaxProgress();
    }
    
    // Поиск проектов с минимальным прогрессом
    @Transactional(readOnly = true)
    public List<Project> findProjectsWithMinProgress() {
        return projectRepository.findProjectsWithMinProgress();
    }
    
    // Поиск просроченных проектов
    @Transactional(readOnly = true)
    public List<Project> findOverdueProjects(LocalDate currentDate) {
        return projectRepository.findOverdueProjects(currentDate);
    }
    
    // Поиск проектов, которые должны завершиться в ближайшие дни
    @Transactional(readOnly = true)
    public List<Project> findProjectsEndingSoon(LocalDate startDate, LocalDate endDate) {
        return projectRepository.findProjectsEndingSoon(startDate, endDate);
    }
}