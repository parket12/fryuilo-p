package com.example.project2.controller;

import com.example.project2.model.User;
import com.example.project2.service.AuthService;
import com.example.project2.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            return "auth/register";
        }
        
        if (authService.usernameExists(user.getUsername())) {
            result.rejectValue("username", "error.user", "Пользователь с таким логином уже существует");
            return "auth/register";
        }
        
        if (userService.phoneExists(user.getPhone())) {
            result.rejectValue("phone", "error.user", "Пользователь с таким телефоном уже существует");
            return "auth/register";
        }
        
        try {
            authService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно! Теперь вы можете войти в систему.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при регистрации: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }
    
    // Обработка входа
    @PostMapping("/login")
    public String loginUser(@ModelAttribute("loginRequest") LoginRequest loginRequest,
                           BindingResult result,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            result.rejectValue("username", "error.loginRequest", "Логин не может быть пустым");
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            result.rejectValue("password", "error.loginRequest", "Пароль не может быть пустым");
        }
        
        if (result.hasErrors()) {
            return "auth/login";
        }
        
        try {
            User user = authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            if (user != null) {
                session.setAttribute("user", user);
                session.setAttribute("isAuthenticated", true);
                redirectAttributes.addFlashAttribute("successMessage", "Добро пожаловать, " + user.getFirstName() + "!");
                
                // Перенаправляем пользователя url на страницу приветствия, остальных на главную
                if ("url".equals(user.getUsername())) {
                    return "redirect:/welcome";
                } else {
                    return "redirect:/";
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Неверный логин или пароль");
                return "redirect:/auth/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при входе: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/logout")
    public String logoutUser(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Вы успешно вышли из системы");
        return "redirect:/auth/login";
    }
    
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
