package com.example.project2.service;

import com.example.project2.model.User;
import com.example.project2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Регистрация нового пользователя
    public User registerUser(User user) {
        // Проверяем, что пользователь с таким логином не существует
        if (usernameExists(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }
        
        // Проверяем, что пользователь с таким email не существует
        if (userService.emailExists(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        
        // Проверяем, что пользователь с таким телефоном не существует
        if (userService.phoneExists(user.getPhone())) {
            throw new RuntimeException("Пользователь с таким телефоном уже существует");
        }
        
        // Хешируем пароль
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        
        // Сохраняем пользователя
        return userService.createUser(user);
    }
    
    @Transactional(readOnly = true)
    public User authenticateUser(String username, String password) {
        Optional<User> userOpt = findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (passwordEncoder.matches(password, user.getPassword())) {
                if (user.getIsActive()) {
                    return user;
                } else {
                    throw new RuntimeException("Аккаунт заблокирован");
                }
            } else {
                throw new RuntimeException("Неверный пароль");
            }
        } else {
            throw new RuntimeException("Пользователь с таким логином не найден");
        }
    }
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Изменение пароля
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userService.getUserById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Проверяем старый пароль
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                // Хешируем новый пароль
                String hashedNewPassword = passwordEncoder.encode(newPassword);
                user.setPassword(hashedNewPassword);
                userService.createUser(user); // Обновляем пользователя
                return true;
            } else {
                throw new RuntimeException("Неверный старый пароль");
            }
        } else {
            throw new RuntimeException("Пользователь не найден");
        }
    }
    
    // Сброс пароля (для администратора)
    public boolean resetPassword(Long userId, String newPassword) {
        Optional<User> userOpt = userService.getUserById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedPassword);
            userService.createUser(user);
            return true;
        } else {
            throw new RuntimeException("Пользователь не найден");
        }
    }
}
