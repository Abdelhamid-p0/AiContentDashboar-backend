package com.quiz.ai.quizModule.service.impl;

import org.springframework.stereotype.Service;

import com.quiz.ai.quizModule.dto.course.CourseResponse;
import com.quiz.ai.quizModule.dto.course.PagedCoursesResponse;
import com.quiz.ai.quizModule.entity.subject.Course;
import com.quiz.ai.quizModule.enums.Semester;
import com.quiz.ai.quizModule.repository.CourseRepository;
import com.quiz.ai.quizModule.service.CourseService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
public class DefaultCourseService implements CourseService {
    private final CourseRepository courseRepository;

    public DefaultCourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public PagedCoursesResponse getCourses(String levelId, String subjectId, Semester semester, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Course> coursesPage = courseRepository.findAllFiltered(levelId, subjectId, semester, pageable);
        List<CourseResponse> response = coursesPage.getContent().stream().map(CourseResponse::fromEntity).toList();
        return new PagedCoursesResponse(response, coursesPage.getNumber(), coursesPage.getSize(),
                coursesPage.getTotalElements(), coursesPage.getTotalPages());
    }
}