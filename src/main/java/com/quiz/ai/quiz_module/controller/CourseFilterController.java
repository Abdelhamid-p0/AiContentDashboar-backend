package com.quiz.ai.quiz_module.controller;

import com.quiz.ai.quiz_module.dto.course.CourseFiltersResponse;
import com.quiz.ai.quiz_module.dto.course.FilterOptionResponse;
import com.quiz.ai.quiz_module.enums.Semester;
import com.quiz.ai.quiz_module.repository.LevelRepository;
import com.quiz.ai.quiz_module.repository.SubjectRepository;
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
                                                .map(level -> new FilterOptionResponse(level.getId(),
                                                                level.getLevelName()))
                                                .toList(),
                                subjectRepository.findAll().stream()
                                                .map(subject -> new FilterOptionResponse(subject.getId(),
                                                                subject.getTitle()))
                                                .toList(),
                                Arrays.stream(Semester.values())
                                                .map(semester -> new FilterOptionResponse(semester.name(),
                                                                semester.name()))
                                                .toList());
        }
}
