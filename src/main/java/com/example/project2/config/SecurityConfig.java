package com.example.project2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**", "/", "/welcome", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .anyRequest().permitAll() // Временно разрешаем доступ ко всем страницам
            )
            .headers(headers -> headers.frameOptions().disable()) // Для H2 консоли
            .formLogin(form -> form.disable()) // Отключаем стандартную форму входа Spring Security
            .logout(logout -> logout.disable()); // Отключаем стандартный logout Spring Security

        return http.build();
    }
}
