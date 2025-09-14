package com.example.project2.controller;

import com.example.project2.model.Project;
import com.example.project2.service.ProjectService;
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
@RequestMapping("/projects")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    // Показать все проекты
    @GetMapping
    public String getAllProjects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "projects/list";
    }
    
    // Показать форму создания проекта
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Project project = new Project();
        model.addAttribute("project", project);
        return "projects/create";
    }
    
    // Создать проект
    @PostMapping("/create")
    public String createProject(@Valid @ModelAttribute("project") Project project, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "projects/create";
        }
        
        // Проверка уникальности названия
        if (projectService.projectNameExists(project.getName())) {
            result.rejectValue("name", "error.project", "Проект с таким названием уже существует");
            return "projects/create";
        }
        
        projectService.createProject(project);
        redirectAttributes.addFlashAttribute("successMessage", "Проект успешно создан");
        return "redirect:/projects";
    }
    
    // Показать проект по ID
    @GetMapping("/{id}")
    public String getProjectById(@PathVariable Long id, Model model) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            return "projects/details";
        } else {
            return "redirect:/projects";
        }
    }
    
    // Показать форму редактирования проекта
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            return "projects/edit";
        } else {
            return "redirect:/projects";
        }
    }
    
    // Обновить проект
    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable Long id, 
                              @Valid @ModelAttribute("project") Project project, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "projects/edit";
        }
        
        // Проверка уникальности названия (исключая текущий проект)
        Optional<Project> existingProject = projectService.getProjectByName(project.getName());
        if (existingProject.isPresent() && !existingProject.get().getId().equals(id)) {
            result.rejectValue("name", "error.project", "Проект с таким названием уже существует");
            return "projects/edit";
        }
        
        Project updatedProject = projectService.updateProject(id, project);
        if (updatedProject != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Проект успешно обновлен");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении проекта");
        }
        return "redirect:/projects";
    }
    
    // Удалить проект
    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = projectService.deleteProject(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Проект успешно удален");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении проекта");
        }
        return "redirect:/projects";
    }
    
    // Активировать проект
    @PostMapping("/{id}/activate")
    public String activateProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean activated = projectService.restoreProject(id);
        if (activated) {
            redirectAttributes.addFlashAttribute("successMessage", "Проект успешно активирован");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при активации проекта");
        }
        return "redirect:/projects";
    }
    
    // Деактивировать проект
    @PostMapping("/{id}/deactivate")
    public String deactivateProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deactivated = projectService.softDeleteProject(id);
        if (deactivated) {
            redirectAttributes.addFlashAttribute("successMessage", "Проект успешно деактивирован");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при деактивации проекта");
        }
        return "redirect:/projects";
    }
    
    // Поиск проектов
    @GetMapping("/search")
    public String searchProjects(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String priority,
                               @RequestParam(required = false) Integer minProgress,
                               @RequestParam(required = false) Integer maxProgress,
                               Model model) {
        
        List<Project> projects;
        
        if (name != null && !name.isEmpty()) {
            projects = projectService.searchProjects(name, null, null, null, null, null, null, null, null, 0, 100, "id", "asc").getContent();
        } else if (description != null && !description.isEmpty()) {
            projects = projectService.searchProjects(description, null, null, null, null, null, null, null, null, 0, 100, "id", "asc").getContent();
        } else if (status != null && !status.isEmpty()) {
            projects = projectService.findByStatus(status, 0, 100, "id", "asc").getContent();
        } else if (priority != null && !priority.isEmpty()) {
            projects = projectService.findByPriority(priority, 0, 100, "id", "asc").getContent();
        } else if (minProgress != null && maxProgress != null) {
            projects = projectService.findByProgressBetween(minProgress, maxProgress, 0, 100, "id", "asc").getContent();
        } else {
            projects = projectService.getAllProjects();
        }
        
        model.addAttribute("projects", projects);
        return "projects/search";
    }
}
