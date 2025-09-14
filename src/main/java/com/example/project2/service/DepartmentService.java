package com.example.project2.service;

import com.example.project2.model.Department;
import com.example.project2.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    // Создать отдел
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }
    
    // Получить все отделы с пагинацией
    @Transactional(readOnly = true)
    public Page<Department> getAllDepartments(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findActiveDepartments(pageable);
    }
    
    // Получить все отделы
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    // Получить активные отделы
    @Transactional(readOnly = true)
    public List<Department> getActiveDepartments() {
        return departmentRepository.findActiveDepartments();
    }
    
    // Получить отдел по ID
    @Transactional(readOnly = true)
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }
    
    // Получить отдел по названию
    @Transactional(readOnly = true)
    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }
    
    // Получить отдел по коду
    @Transactional(readOnly = true)
    public Optional<Department> getDepartmentByCode(String code) {
        return departmentRepository.findByCode(code);
    }
    
    // Обновить отдел
    public Department updateDepartment(Long id, Department departmentDetails) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            department.setName(departmentDetails.getName());
            department.setDescription(departmentDetails.getDescription());
            department.setCode(departmentDetails.getCode());
            department.setBudget(departmentDetails.getBudget());
            department.setEmployeeCount(departmentDetails.getEmployeeCount());
            department.setIsActive(departmentDetails.getIsActive());
            return departmentRepository.save(department);
        }
        return null;
    }
    
    // Физическое удаление отдела
    public boolean deleteDepartment(Long id) {
        if (departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Логическое удаление отдела
    public boolean softDeleteDepartment(Long id) {
        int result = departmentRepository.softDeleteById(id, LocalDateTime.now());
        return result > 0;
    }
    
    // Восстановление отдела из логического удаления
    public boolean restoreDepartment(Long id) {
        int result = departmentRepository.restoreById(id);
        return result > 0;
    }
    
    // Поиск с фильтрами и пагинацией
    @Transactional(readOnly = true)
    public Page<Department> searchDepartments(String search, Double minBudget, Double maxBudget, 
                                            Integer minEmployeeCount, Integer maxEmployeeCount,
                                            int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return departmentRepository.findDepartmentsWithFilters(search, minBudget, maxBudget, 
                                                             minEmployeeCount, maxEmployeeCount, pageable);
    }
    
    // Поиск по бюджету с пагинацией
    @Transactional(readOnly = true)
    public Page<Department> findByBudgetGreaterThan(Double budget, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findByBudgetGreaterThan(budget, pageable);
    }
    
    // Поиск по количеству сотрудников с пагинацией
    @Transactional(readOnly = true)
    public Page<Department> findByEmployeeCountGreaterThan(Integer employeeCount, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findByEmployeeCountGreaterThan(employeeCount, pageable);
    }
    
    // Поиск отделов с бюджетом в диапазоне с пагинацией
    @Transactional(readOnly = true)
    public Page<Department> findByBudgetBetween(Double minBudget, Double maxBudget, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findByBudgetBetween(minBudget, maxBudget, pageable);
    }
    
    // Поиск отделов с количеством сотрудников в диапазоне с пагинацией
    @Transactional(readOnly = true)
    public Page<Department> findByEmployeeCountBetween(Integer minCount, Integer maxCount, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findByEmployeeCountBetween(minCount, maxCount, pageable);
    }
    
    // Поиск удаленных отделов
    @Transactional(readOnly = true)
    public Page<Department> findDeletedDepartments(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findDeletedDepartments(pageable);
    }
    
    // Проверка существования отдела по названию
    @Transactional(readOnly = true)
    public boolean departmentNameExists(String name) {
        return departmentRepository.existsByName(name);
    }
    
    // Проверка существования отдела по коду
    @Transactional(readOnly = true)
    public boolean departmentCodeExists(String code) {
        return departmentRepository.existsByCode(code);
    }
    
    // Проверка существования отдела
    @Transactional(readOnly = true)
    public boolean departmentExists(Long id) {
        return departmentRepository.existsById(id);
    }
    
    // Подсчет количества активных отделов
    @Transactional(readOnly = true)
    public Long countActiveDepartments() {
        return departmentRepository.countActiveDepartments();
    }
    
    // Подсчет общего количества сотрудников во всех отделах
    @Transactional(readOnly = true)
    public Long sumActiveEmployeeCount() {
        return departmentRepository.sumActiveEmployeeCount();
    }
}

