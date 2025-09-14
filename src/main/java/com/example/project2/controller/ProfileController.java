package com.example.project2.controller;

import com.example.project2.model.Profile;
import com.example.project2.model.User;
import com.example.project2.service.ProfileService;
import com.example.project2.service.UserService;
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
@RequestMapping("/profiles")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String getAllProfiles(Model model) {
        List<Profile> profiles = profileService.getAllProfiles();
        model.addAttribute("profiles", profiles);
        return "profiles/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Profile profile = new Profile();
        List<User> users = userService.getAllUsers();
        model.addAttribute("profile", profile);
        model.addAttribute("users", users);
        return "profiles/create";
    }
    
    @PostMapping("/create")
    public String createProfile(@Valid @ModelAttribute("profile") Profile profile, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "profiles/create";
        }
        
        // Проверка, что у пользователя еще нет профиля
        if (profile.getUser() != null && profileService.profileExistsForUser(profile.getUser().getId())) {
            result.rejectValue("user", "error.profile", "У этого пользователя уже есть профиль");
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "profiles/create";
        }
        
        profileService.createProfile(profile);
        redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно создан");
        return "redirect:/profiles";
    }
    
    // Показать профиль по ID
    @GetMapping("/{id}")
    public String getProfileById(@PathVariable Long id, Model model) {
        Optional<Profile> profile = profileService.getProfileById(id);
        if (profile.isPresent()) {
            model.addAttribute("profile", profile.get());
            return "profiles/details";
        } else {
            return "redirect:/profiles";
        }
    }
    
    // Показать форму редактирования профиля
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Profile> profile = profileService.getProfileById(id);
        if (profile.isPresent()) {
            List<User> users = userService.getAllUsers();
            model.addAttribute("profile", profile.get());
            model.addAttribute("users", users);
            return "profiles/edit";
        } else {
            return "redirect:/profiles";
        }
    }
    
    // Обновить профиль
    @PostMapping("/{id}/edit")
    public String updateProfile(@PathVariable Long id, 
                              @Valid @ModelAttribute("profile") Profile profile, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "profiles/edit";
        }
        
        Profile updatedProfile = profileService.updateProfile(id, profile);
        if (updatedProfile != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении профиля");
        }
        return "redirect:/profiles";
    }
    
    // Удалить профиль
    @PostMapping("/{id}/delete")
    public String deleteProfile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = profileService.deleteProfile(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно удален");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении профиля");
        }
        return "redirect:/profiles";
    }
    
    // Поиск профилей
    @GetMapping("/search")
    public String searchProfiles(@RequestParam(required = false) String city,
                               @RequestParam(required = false) String country,
                               @RequestParam(required = false) String postalCode,
                               Model model) {
        
        List<Profile> profiles;
        
        if (city != null && !city.isEmpty()) {
            profiles = profileService.findByCity(city, 0, 100, "id", "asc").getContent();
        } else if (country != null && !country.isEmpty()) {
            profiles = profileService.findByCountry(country, 0, 100, "id", "asc").getContent();
        } else if (postalCode != null && !postalCode.isEmpty()) {
            profiles = profileService.findByPostalCode(postalCode, 0, 100, "id", "asc").getContent();
        } else {
            profiles = profileService.getAllProfiles();
        }
        
        model.addAttribute("profiles", profiles);
        return "profiles/search";
    }
}
