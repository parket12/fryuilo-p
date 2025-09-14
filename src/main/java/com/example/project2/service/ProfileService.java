package com.example.project2.service;

import com.example.project2.model.Profile;
import com.example.project2.repository.ProfileRepository;
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
public class ProfileService {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    // Создать профиль
    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }
    
    // Получить все профили с пагинацией
    @Transactional(readOnly = true)
    public Page<Profile> getAllProfiles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return profileRepository.findAllProfiles(pageable);
    }
    
    // Получить все профили
    @Transactional(readOnly = true)
    public List<Profile> getAllProfiles() {
        return profileRepository.findAllProfiles();
    }
    
    // Получить профиль по ID
    @Transactional(readOnly = true)
    public Optional<Profile> getProfileById(Long id) {
        return profileRepository.findById(id);
    }
    
    // Получить профиль по пользователю
    @Transactional(readOnly = true)
    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }
    
    // Обновить профиль
    public Profile updateProfile(Long id, Profile profileDetails) {
        Optional<Profile> optionalProfile = profileRepository.findById(id);
        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            profile.setAddress(profileDetails.getAddress());
            profile.setCity(profileDetails.getCity());
            profile.setCountry(profileDetails.getCountry());
            profile.setPostalCode(profileDetails.getPostalCode());
            profile.setBiography(profileDetails.getBiography());
            return profileRepository.save(profile);
        }
        return null;
    }
    
    // Физическое удаление профиля
    public boolean deleteProfile(Long id) {
        if (profileRepository.existsById(id)) {
            profileRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Логическое удаление профиля
    public boolean softDeleteProfile(Long id) {
        int result = profileRepository.softDeleteById(id, LocalDateTime.now());
        return result > 0;
    }
    
    // Восстановление профиля из логического удаления
    public boolean restoreProfile(Long id) {
        int result = profileRepository.restoreById(id);
        return result > 0;
    }
    
    // Поиск с фильтрами и пагинацией
    @Transactional(readOnly = true)
    public Page<Profile> searchProfiles(String search, String city, String country, String postalCode,
                                       int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return profileRepository.findProfilesWithFilters(search, city, country, postalCode, pageable);
    }
    
    // Поиск по городу с пагинацией
    @Transactional(readOnly = true)
    public Page<Profile> findByCity(String city, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return profileRepository.findByCity(city, pageable);
    }
    
    // Поиск по стране с пагинацией
    @Transactional(readOnly = true)
    public Page<Profile> findByCountry(String country, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return profileRepository.findByCountry(country, pageable);
    }
    
    // Поиск по городу и стране с пагинацией
    @Transactional(readOnly = true)
    public Page<Profile> findByCityAndCountry(String city, String country, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return profileRepository.findByCityAndCountry(city, country, pageable);
    }
    
    // Поиск по почтовому индексу с пагинацией
    @Transactional(readOnly = true)
    public Page<Profile> findByPostalCode(String postalCode, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return profileRepository.findByPostalCode(postalCode, pageable);
    }
    
    // Поиск удаленных профилей
    @Transactional(readOnly = true)
    public Page<Profile> findDeletedProfiles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return profileRepository.findDeletedProfiles(pageable);
    }
    
    // Проверка существования профиля для пользователя
    @Transactional(readOnly = true)
    public boolean profileExistsForUser(Long userId) {
        return profileRepository.existsByUserId(userId);
    }
    
    // Проверка существования профиля
    @Transactional(readOnly = true)
    public boolean profileExists(Long id) {
        return profileRepository.existsById(id);
    }
}

