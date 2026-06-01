package com.quiz.ai.interfaces.rest;

import com.quiz.ai.application.dto.course.CourseFiltersResponse;
import com.quiz.ai.application.dto.course.FilterOptionResponse;
import com.quiz.ai.enums.Semester;
import com.quiz.ai.repository.LevelRepository;
import com.quiz.ai.repository.SubjectRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/courses/filters")
public class CourseFilterController {
    private final LevelRepository levelRepository;
    private final SubjectRepository subjectRepository;

    public CourseFilterController(LevelRepository levelRepository, SubjectRepository subjectRepository) {
        this.levelRepository = levelRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping
    public CourseFiltersResponse getFilters() {
        return new CourseFiltersResponse(
                levelRepository.findAll().stream()
                        .map(level -> new FilterOptionResponse(level.getId(), level.getLevelName()))
                        .toList(),
                subjectRepository.findAll().stream()
                        .map(subject -> new FilterOptionResponse(subject.getId(), subject.getTitle()))
                        .toList(),
                Arrays.stream(Semester.values())
                        .map(semester -> new FilterOptionResponse(semester.name(), semester.name()))
                        .toList());
    }
}
