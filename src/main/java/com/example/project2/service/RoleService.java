package com.example.project2.service;

import com.example.project2.model.Role;
import com.example.project2.repository.RoleRepository;
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
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    // Создать роль
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
    
    // Получить все роли с пагинацией
    @Transactional(readOnly = true)
    public Page<Role> getAllRoles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return roleRepository.findActiveRoles(pageable);
    }
    
    // Получить все роли
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    // Получить активные роли
    @Transactional(readOnly = true)
    public List<Role> getActiveRoles() {
        return roleRepository.findActiveRoles();
    }
    
    // Получить роль по ID
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
    
    // Получить роль по названию
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
    
    // Получить роль по коду
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByCode(String code) {
        return roleRepository.findByCode(code);
    }
    
    // Обновить роль
    public Role updateRole(Long id, Role roleDetails) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            role.setName(roleDetails.getName());
            role.setDescription(roleDetails.getDescription());
            role.setCode(roleDetails.getCode());
            role.setAccessLevel(roleDetails.getAccessLevel());
            role.setIsActive(roleDetails.getIsActive());
            role.setIsSystem(roleDetails.getIsSystem());
            return roleRepository.save(role);
        }
        return null;
    }
    
    // Физическое удаление роли
    public boolean deleteRole(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Логическое удаление роли
    public boolean softDeleteRole(Long id) {
        int result = roleRepository.softDeleteById(id, LocalDateTime.now());
        return result > 0;
    }
    
    // Восстановление роли из логического удаления
    public boolean restoreRole(Long id) {
        int result = roleRepository.restoreById(id);
        return result > 0;
    }
    
    // Поиск с фильтрами и пагинацией
    @Transactional(readOnly = true)
    public Page<Role> searchRoles(String search, Boolean isSystem, Integer minAccessLevel, Integer maxAccessLevel,
                                 int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return roleRepository.findRolesWithFilters(search, isSystem, minAccessLevel, maxAccessLevel, pageable);
    }
    
    // Поиск по уровню доступа с пагинацией
    @Transactional(readOnly = true)
    public Page<Role> findByAccessLevel(Integer accessLevel, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return roleRepository.findByAccessLevel(accessLevel, pageable);
    }
    
    // Поиск ролей с уровнем доступа в диапазоне с пагинацией
    @Transactional(readOnly = true)
    public Page<Role> findByAccessLevelBetween(Integer minLevel, Integer maxLevel, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return roleRepository.findByAccessLevelBetween(minLevel, maxLevel, pageable);
    }
    
    // Поиск системных ролей с пагинацией
    @Transactional(readOnly = true)
    public Page<Role> getSystemRoles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return roleRepository.findSystemRoles(pageable);
    }
    
    // Поиск пользовательских ролей с пагинацией
    @Transactional(readOnly = true)
    public Page<Role> getUserRoles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return roleRepository.findUserRoles(pageable);
    }
    
    // Поиск удаленных ролей
    @Transactional(readOnly = true)
    public Page<Role> findDeletedRoles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return roleRepository.findDeletedRoles(pageable);
    }
    
    // Проверка существования роли по названию
    @Transactional(readOnly = true)
    public boolean roleNameExists(String name) {
        return roleRepository.existsByName(name);
    }
    
    // Проверка существования роли по коду
    @Transactional(readOnly = true)
    public boolean roleCodeExists(String code) {
        return roleRepository.existsByCode(code);
    }
    
    // Проверка существования роли
    @Transactional(readOnly = true)
    public boolean roleExists(Long id) {
        return roleRepository.existsById(id);
    }
    
    // Подсчет количества активных ролей
    @Transactional(readOnly = true)
    public Long countActiveRoles() {
        return roleRepository.countActiveRoles();
    }
    
    // Поиск ролей с максимальным уровнем доступа
    @Transactional(readOnly = true)
    public List<Role> findRolesWithMaxAccessLevel() {
        return roleRepository.findRolesWithMaxAccessLevel();
    }
    
    // Поиск ролей с минимальным уровнем доступа
    @Transactional(readOnly = true)
    public List<Role> findRolesWithMinAccessLevel() {
        return roleRepository.findRolesWithMinAccessLevel();
    }
}

