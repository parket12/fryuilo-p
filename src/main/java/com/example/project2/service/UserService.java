package com.example.project2.service;

import com.example.project2.model.User;
import com.example.project2.repository.UserRepository;
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
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findActiveUsers(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findActiveUsers();
    }
    
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setEmail(userDetails.getEmail());
            user.setPhone(userDetails.getPhone());
            user.setBirthDate(userDetails.getBirthDate());
            user.setAge(userDetails.getAge());
            user.setDepartment(userDetails.getDepartment());
            user.setRoles(userDetails.getRoles());
            user.setProjects(userDetails.getProjects());
            return userRepository.save(user);
        }
        return null;
    }
    
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public boolean softDeleteUser(Long id) {
        int result = userRepository.softDeleteById(id, LocalDateTime.now());
        return result > 0;
    }
    
    public boolean restoreUser(Long id) {
        int result = userRepository.restoreById(id);
        return result > 0;
    }
    
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String search, Long departmentId, Long roleId, Long projectId, 
                                 Integer minAge, Integer maxAge, int page, int size, 
                                 String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return userRepository.findUsersWithFilters(search, departmentId, roleId, projectId, 
                                                  minAge, maxAge, pageable);
    }
    
    // Поиск по email
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Поиск по телефону
    @Transactional(readOnly = true)
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
    
    // Поиск по имени и фамилии с пагинацией
    @Transactional(readOnly = true)
    public Page<User> findByFirstNameAndLastName(String firstName, String lastName, 
                                                int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByFirstNameAndLastName(firstName, lastName, pageable);
    }
    
    // Поиск по отделу с пагинацией
    @Transactional(readOnly = true)
    public Page<User> findByDepartment(Long departmentId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByDepartmentId(departmentId, pageable);
    }
    
    // Поиск активных пользователей
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }
    
    // Поиск пользователей с определенной ролью с пагинацией
    @Transactional(readOnly = true)
    public Page<User> findByRole(Long roleId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByRoleId(roleId, pageable);
    }
    
    // Поиск пользователей в определенном проекте с пагинацией
    @Transactional(readOnly = true)
    public Page<User> findByProject(Long projectId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByProjectId(projectId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<User> findByAgeRange(Integer minAge, Integer maxAge, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByAgeBetween(minAge, maxAge, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<User> findDeletedUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findDeletedUsers(pageable);
    }
    
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }
    
    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
}

