package com.example.project2.config;

import com.example.project2.model.User;
import com.example.project2.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AuthService authService;

    @Override
    public void run(String... args) throws Exception {
        // Создаем пользователя admin
        if (!authService.usernameExists("admin")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Administrator");
            admin.setEmail("admin@example.com");
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setPhone("79990000001");
            admin.setBirthDate(LocalDate.of(1990, 1, 1));
            admin.setAge(34);
            
            try {
                authService.registerUser(admin);
                System.out.println("✅ Пользователь 'admin' создан успешно!");
            } catch (Exception e) {
                System.out.println("❌ Ошибка при создании пользователя 'admin': " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ Пользователь 'admin' уже существует");
        }

        // Создаем пользователя url
        if (!authService.usernameExists("url")) {
            User url = new User();
            url.setFirstName("Url");
            url.setLastName("User");
            url.setEmail("url@example.com");
            url.setUsername("url");
            url.setPassword("url");
            url.setPhone("79990000002");
            url.setBirthDate(LocalDate.of(1995, 5, 15));
            url.setAge(29);
            
            try {
                authService.registerUser(url);
                System.out.println("✅ Пользователь 'url' создан успешно!");
            } catch (Exception e) {
                System.out.println("❌ Ошибка при создании пользователя 'url': " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ Пользователь 'url' уже существует");
        }
    }
}
