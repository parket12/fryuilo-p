package com.example.project2.controller;

import com.example.project2.model.StudentModel;
import com.example.project2.service.InMemoryStudentImpl;
import com.example.project2.service.StudentService;
import com.example.project2.service.StudentServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/student")
public class StudentApiController {

    private final StudentService studentService;

    public StudentApiController(StudentServiceImpl studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentModel> getAllStudents() {
        return studentService.findAllStudent();
    }

    @PostMapping
    public StudentModel createStudent(@RequestBody StudentModel student) {
        return studentService.createStudent(student);
    }

    @DeleteMapping
    public void deleteStudent(@RequestBody StudentModel student) {
         studentService.deleteStudent(student.getId());
         return;
    }
}
