package com.example.project2.controller;

import com.example.project2.model.User;
import com.example.project2.model.Department;
import com.example.project2.model.Role;
import com.example.project2.model.Project;
import com.example.project2.service.UserService;
import com.example.project2.service.DepartmentService;
import com.example.project2.service.RoleService;
import com.example.project2.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private ProjectService projectService;
    
    // Показать всех пользователей с пагинацией
    @GetMapping
    public String getAllUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "id") String sortBy,
                            @RequestParam(defaultValue = "asc") String sortDir,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) Long departmentId,
                            @RequestParam(required = false) Long roleId,
                            @RequestParam(required = false) Long projectId,
                            @RequestParam(required = false) Integer minAge,
                            @RequestParam(required = false) Integer maxAge,
                            Model model) {
        
        Page<User> users;
        
        if (search != null || departmentId != null || roleId != null || projectId != null || 
            minAge != null || maxAge != null) {
            // Поиск с фильтрами
            users = userService.searchUsers(search, departmentId, roleId, projectId, 
                                          minAge, maxAge, page, size, sortBy, sortDir);
        } else {
            // Обычный список с пагинацией
            users = userService.getAllUsers(page, size, sortBy, sortDir);
        }
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalItems", users.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("roleId", roleId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("minAge", minAge);
        model.addAttribute("maxAge", maxAge);
        
        return "users/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        User user = new User();
        List<Department> departments = departmentService.getActiveDepartments();
        List<Role> roles = roleService.getActiveRoles();
        List<Project> projects = projectService.getActiveProjects();
        
        model.addAttribute("user", user);
        model.addAttribute("departments", departments);
        model.addAttribute("roles", roles);
        model.addAttribute("projects", projects);
        return "users/create";
    }
    
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") User user, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            List<Department> departments = departmentService.getActiveDepartments();
            List<Role> roles = roleService.getActiveRoles();
            List<Project> projects = projectService.getActiveProjects();
            
            model.addAttribute("departments", departments);
            model.addAttribute("roles", roles);
            model.addAttribute("projects", projects);
            return "users/create";
        }
        
        // Проверка уникальности email
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            List<Department> departments = departmentService.getActiveDepartments();
            List<Role> roles = roleService.getActiveRoles();
            List<Project> projects = projectService.getActiveProjects();
            
            model.addAttribute("departments", departments);
            model.addAttribute("roles", roles);
            model.addAttribute("projects", projects);
            return "users/create";
        }
        
        // Проверка уникальности телефона
        if (userService.phoneExists(user.getPhone())) {
            result.rejectValue("phone", "error.user", "Пользователь с таким телефоном уже существует");
            List<Department> departments = departmentService.getActiveDepartments();
            List<Role> roles = roleService.getActiveRoles();
            List<Project> projects = projectService.getActiveProjects();
            
            model.addAttribute("departments", departments);
            model.addAttribute("roles", roles);
            model.addAttribute("projects", projects);
            return "users/create";
        }
        
        userService.createUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно создан");
        return "redirect:/users";
    }
    
    // Показать пользователя по ID
    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "users/details";
        } else {
            return "redirect:/users";
        }
    }
    
    // Показать форму редактирования пользователя
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            List<Department> departments = departmentService.getActiveDepartments();
            List<Role> roles = roleService.getActiveRoles();
            List<Project> projects = projectService.getActiveProjects();
            
            model.addAttribute("user", user.get());
            model.addAttribute("departments", departments);
            model.addAttribute("roles", roles);
            model.addAttribute("projects", projects);
            return "users/edit";
        } else {
            return "redirect:/users";
        }
    }
    
    // Обновить пользователя
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, 
                           @Valid @ModelAttribute("user") User user, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            List<Department> departments = departmentService.getActiveDepartments();
            List<Role> roles = roleService.getActiveRoles();
            List<Project> projects = projectService.getActiveProjects();
            
            model.addAttribute("departments", departments);
            model.addAttribute("roles", roles);
            model.addAttribute("projects", projects);
            return "users/edit";
        }
        
        // Проверка уникальности email (исключая текущего пользователя)
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            result.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            List<Department> departments = departmentService.getActiveDepartments();
            List<Role> roles = roleService.getActiveRoles();
            List<Project> projects = projectService.getActiveProjects();
            
            model.addAttribute("departments", departments);
            model.addAttribute("roles", roles);
            model.addAttribute("projects", projects);
            return "users/edit";
        }
        
        // Проверка уникальности телефона (исключая текущего пользователя)
        Optional<User> existingUserByPhone = userService.findByPhone(user.getPhone());
        if (existingUserByPhone.isPresent() && !existingUserByPhone.get().getId().equals(id)) {
            result.rejectValue("phone", "error.user", "Пользователь с таким телефоном уже существует");
            List<Department> departments = departmentService.getActiveDepartments();
            List<Role> roles = roleService.getActiveRoles();
            List<Project> projects = projectService.getActiveProjects();
            
            model.addAttribute("departments", departments);
            model.addAttribute("roles", roles);
            model.addAttribute("projects", projects);
            return "users/edit";
        }
        
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно обновлен");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении пользователя");
        }
        return "redirect:/users";
    }
    
    // Физическое удаление пользователя
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удален");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении пользователя");
        }
        return "redirect:/users";
    }
    
    // Логическое удаление пользователя
    @PostMapping("/{id}/soft-delete")
    public String softDeleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = userService.softDeleteUser(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удален");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении пользователя");
        }
        return "redirect:/users";
    }
    
    // Восстановление пользователя
    @PostMapping("/{id}/restore")
    public String restoreUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean restored = userService.restoreUser(id);
        if (restored) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно восстановлен");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при восстановлении пользователя");
        }
        return "redirect:/users";
    }
    
    // Показать удаленных пользователей
    @GetMapping("/deleted")
    public String getDeletedUsers(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(defaultValue = "desc") String sortDir,
                                Model model) {
        Page<User> users = userService.findDeletedUsers(page, size, sortBy, sortDir);
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalItems", users.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        
        return "users/deleted";
    }
    
    // Поиск пользователей
    @GetMapping("/search")
    public String searchUsers(@RequestParam(required = false) String firstName,
                            @RequestParam(required = false) String lastName,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false) Long departmentId,
                            @RequestParam(required = false) Long roleId,
                            @RequestParam(required = false) Long projectId,
                            Model model) {
        
        List<User> users;
        
        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            users = userService.findByFirstNameAndLastName(firstName, lastName, 0, 100, "id", "asc").getContent();
        } else if (email != null && !email.isEmpty()) {
            Optional<User> user = userService.findByEmail(email);
            users = user.map(u -> java.util.Arrays.asList(u)).orElse(java.util.Collections.emptyList());
        } else if (departmentId != null) {
            users = userService.findByDepartment(departmentId, 0, 100, "id", "asc").getContent();
        } else if (roleId != null) {
            users = userService.findByRole(roleId, 0, 100, "id", "asc").getContent();
        } else if (projectId != null) {
            users = userService.findByProject(projectId, 0, 100, "id", "asc").getContent();
        } else {
            users = userService.getAllUsers();
        }
        
        model.addAttribute("users", users);
        model.addAttribute("departments", departmentService.getActiveDepartments());
        model.addAttribute("roles", roleService.getActiveRoles());
        model.addAttribute("projects", projectService.getActiveProjects());
        
        return "users/search";
    }
}
