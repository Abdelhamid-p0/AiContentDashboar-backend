package com.quiz.ai.interfaces.rest;

import com.quiz.ai.application.dto.course.PagedCoursesResponse;
import com.quiz.ai.application.service.CourseService;
import com.quiz.ai.enums.Semester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public PagedCoursesResponse getCourses(
            @RequestParam(value = "level_id", required = false) String levelId,
            @RequestParam(value = "subject_id", required = false) String subjectId,
            @RequestParam(value = "semester", required = false) Semester semester,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return courseService.getCourses(levelId, subjectId, semester, page, size);
    }
}