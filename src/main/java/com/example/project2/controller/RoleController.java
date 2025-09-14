package com.example.project2.controller;

import com.example.project2.model.Role;
import com.example.project2.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/roles")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    // Показать все роли
    @GetMapping
    public String getAllRoles(Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return "roles/list";
    }
    
    // Показать форму создания роли
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Role role = new Role();
        model.addAttribute("role", role);
        return "roles/create";
    }
    
    // Создать роль
    @PostMapping("/create")
    public String createRole(@Valid @ModelAttribute("role") Role role, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "roles/create";
        }
        
        // Проверка уникальности названия
        if (roleService.roleNameExists(role.getName())) {
            result.rejectValue("name", "error.role", "Роль с таким названием уже существует");
            return "roles/create";
        }
        
        // Проверка уникальности кода
        if (roleService.roleCodeExists(role.getCode())) {
            result.rejectValue("code", "error.role", "Роль с таким кодом уже существует");
            return "roles/create";
        }
        
        roleService.createRole(role);
        redirectAttributes.addFlashAttribute("successMessage", "Роль успешно создана");
        return "redirect:/roles";
    }
    
    // Показать роль по ID
    @GetMapping("/{id}")
    public String getRoleById(@PathVariable Long id, Model model) {
        Optional<Role> role = roleService.getRoleById(id);
        if (role.isPresent()) {
            model.addAttribute("role", role.get());
            return "roles/details";
        } else {
            return "redirect:/roles";
        }
    }
    
    // Показать форму редактирования роли
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Role> role = roleService.getRoleById(id);
        if (role.isPresent()) {
            model.addAttribute("role", role.get());
            return "roles/edit";
        } else {
            return "redirect:/roles";
        }
    }
    
    // Обновить роль
    @PostMapping("/{id}/edit")
    public String updateRole(@PathVariable Long id, 
                           @Valid @ModelAttribute("role") Role role, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "roles/edit";
        }
        
        // Проверка уникальности названия (исключая текущую роль)
        Optional<Role> existingRole = roleService.getRoleByName(role.getName());
        if (existingRole.isPresent() && !existingRole.get().getId().equals(id)) {
            result.rejectValue("name", "error.role", "Роль с таким названием уже существует");
            return "roles/edit";
        }
        
        // Проверка уникальности кода (исключая текущую роль)
        Optional<Role> existingRoleByCode = roleService.getRoleByCode(role.getCode());
        if (existingRoleByCode.isPresent() && !existingRoleByCode.get().getId().equals(id)) {
            result.rejectValue("code", "error.role", "Роль с таким кодом уже существует");
            return "roles/edit";
        }
        
        Role updatedRole = roleService.updateRole(id, role);
        if (updatedRole != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Роль успешно обновлена");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении роли");
        }
        return "redirect:/roles";
    }
    
    // Удалить роль
    @PostMapping("/{id}/delete")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = roleService.deleteRole(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Роль успешно удалена");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении роли");
        }
        return "redirect:/roles";
    }
    
    // Активировать роль
    @PostMapping("/{id}/activate")
    public String activateRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean activated = roleService.restoreRole(id);
        if (activated) {
            redirectAttributes.addFlashAttribute("successMessage", "Роль успешно активирована");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при активации роли");
        }
        return "redirect:/roles";
    }
    
    // Деактивировать роль
    @PostMapping("/{id}/deactivate")
    public String deactivateRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deactivated = roleService.softDeleteRole(id);
        if (deactivated) {
            redirectAttributes.addFlashAttribute("successMessage", "Роль успешно деактивирована");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при деактивации роли");
        }
        return "redirect:/roles";
    }
    
    // Поиск ролей
    @GetMapping("/search")
    public String searchRoles(@RequestParam(required = false) String name,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) Integer minAccessLevel,
                            @RequestParam(required = false) Integer maxAccessLevel,
                            @RequestParam(required = false) Boolean isSystem,
                            Model model) {
        
        List<Role> roles;
        
        if (name != null && !name.isEmpty()) {
            roles = roleService.searchRoles(name, null, null, null, 0, 100, "id", "asc").getContent();
        } else if (description != null && !description.isEmpty()) {
            roles = roleService.searchRoles(description, null, null, null, 0, 100, "id", "asc").getContent();
        } else if (minAccessLevel != null && maxAccessLevel != null) {
            roles = roleService.findByAccessLevelBetween(minAccessLevel, maxAccessLevel, 0, 100, "id", "asc").getContent();
        } else if (isSystem != null) {
            roles = isSystem ? roleService.getSystemRoles(0, 100, "id", "asc").getContent() : roleService.getUserRoles(0, 100, "id", "asc").getContent();
        } else {
            roles = roleService.getAllRoles();
        }
        
        model.addAttribute("roles", roles);
        return "roles/search";
    }
}
