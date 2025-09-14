package com.example.project2.service;

import com.example.project2.model.StudentModel;
import com.example.project2.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    public StudentServiceImpl(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StudentModel> findAllStudent() {
        return repository.findAll();
    }

    @Override
    public StudentModel createStudent(StudentModel student) {
        return repository.save(student);
    }

    @Override
    public StudentModel updateStudent(StudentModel student) {
        return repository.save(student);
    }

    @Override
    public StudentModel findStudentById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void deleteStudent(Long id) {
        repository.deleteById(id);
    }
}