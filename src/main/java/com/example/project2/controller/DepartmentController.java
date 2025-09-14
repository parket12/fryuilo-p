package com.example.project2.controller;

import com.example.project2.model.Department;
import com.example.project2.service.DepartmentService;
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
@RequestMapping("/departments")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentService;
    
    // Показать все отделы
    @GetMapping
    public String getAllDepartments(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "departments/list";
    }
    
    // Показать форму создания отдела
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Department department = new Department();
        model.addAttribute("department", department);
        return "departments/create";
    }
    
    @PostMapping("/create")
    public String createDepartment(@Valid @ModelAttribute("department") Department department, 
                                 BindingResult result, 
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "departments/create";
        }
        
        if (departmentService.departmentNameExists(department.getName())) {
            result.rejectValue("name", "error.department", "Отдел с таким названием уже существует");
            return "departments/create";
        }
        
        if (departmentService.departmentCodeExists(department.getCode())) {
            result.rejectValue("code", "error.department", "Отдел с таким кодом уже существует");
            return "departments/create";
        }
        
        departmentService.createDepartment(department);
        redirectAttributes.addFlashAttribute("successMessage", "Отдел успешно создан");
        return "redirect:/departments";
    }
    
    // Показать отдел по ID
    @GetMapping("/{id}")
    public String getDepartmentById(@PathVariable Long id, Model model) {
        Optional<Department> department = departmentService.getDepartmentById(id);
        if (department.isPresent()) {
            model.addAttribute("department", department.get());
            return "departments/details";
        } else {
            return "redirect:/departments";
        }
    }
    
    // Показать форму редактирования отдела
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Department> department = departmentService.getDepartmentById(id);
        if (department.isPresent()) {
            model.addAttribute("department", department.get());
            return "departments/edit";
        } else {
            return "redirect:/departments";
        }
    }
    
    // Обновить отдел
    @PostMapping("/{id}/edit")
    public String updateDepartment(@PathVariable Long id, 
                                 @Valid @ModelAttribute("department") Department department, 
                                 BindingResult result, 
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "departments/edit";
        }
        
        // Проверка уникальности названия (исключая текущий отдел)
        Optional<Department> existingDepartment = departmentService.getDepartmentByName(department.getName());
        if (existingDepartment.isPresent() && !existingDepartment.get().getId().equals(id)) {
            result.rejectValue("name", "error.department", "Отдел с таким названием уже существует");
            return "departments/edit";
        }
        
        // Проверка уникальности кода (исключая текущий отдел)
        Optional<Department> existingDepartmentByCode = departmentService.getDepartmentByCode(department.getCode());
        if (existingDepartmentByCode.isPresent() && !existingDepartmentByCode.get().getId().equals(id)) {
            result.rejectValue("code", "error.department", "Отдел с таким кодом уже существует");
            return "departments/edit";
        }
        
        Department updatedDepartment = departmentService.updateDepartment(id, department);
        if (updatedDepartment != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Отдел успешно обновлен");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении отдела");
        }
        return "redirect:/departments";
    }
    
    // Удалить отдел
    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = departmentService.deleteDepartment(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Отдел успешно удален");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении отдела");
        }
        return "redirect:/departments";
    }
    
    // Активировать отдел
    @PostMapping("/{id}/activate")
    public String activateDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean activated = departmentService.restoreDepartment(id);
        if (activated) {
            redirectAttributes.addFlashAttribute("successMessage", "Отдел успешно активирован");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при активации отдела");
        }
        return "redirect:/departments";
    }
    
    // Деактивировать отдел
    @PostMapping("/{id}/deactivate")
    public String deactivateDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deactivated = departmentService.softDeleteDepartment(id);
        if (deactivated) {
            redirectAttributes.addFlashAttribute("successMessage", "Отдел успешно деактивирован");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при деактивации отдела");
        }
        return "redirect:/departments";
    }
    
    // Поиск отделов
    @GetMapping("/search")
    public String searchDepartments(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String description,
                                  @RequestParam(required = false) Double minBudget,
                                  @RequestParam(required = false) Double maxBudget,
                                  @RequestParam(required = false) Integer minEmployeeCount,
                                  @RequestParam(required = false) Integer maxEmployeeCount,
                                  Model model) {
        
        List<Department> departments;
        
        if (name != null && !name.isEmpty()) {
            departments = departmentService.searchDepartments(name, null, null, null, null, 0, 100, "id", "asc").getContent();
        } else if (description != null && !description.isEmpty()) {
            departments = departmentService.searchDepartments(description, null, null, null, null, 0, 100, "id", "asc").getContent();
        } else if (minBudget != null && maxBudget != null) {
            departments = departmentService.findByBudgetBetween(minBudget, maxBudget, 0, 100, "id", "asc").getContent();
        } else if (minEmployeeCount != null && maxEmployeeCount != null) {
            departments = departmentService.findByEmployeeCountBetween(minEmployeeCount, maxEmployeeCount, 0, 100, "id", "asc").getContent();
        } else {
            departments = departmentService.getAllDepartments();
        }
        
        model.addAttribute("departments", departments);
        return "departments/search";
    }
}
