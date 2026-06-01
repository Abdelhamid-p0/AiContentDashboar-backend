package com.quiz.ai.application.service;

import com.quiz.ai.application.dto.course.PagedCoursesResponse;
import com.quiz.ai.enums.Semester;

public interface CourseService {
    PagedCoursesResponse getCourses(String levelId, String subjectId, Semester semester, int page, int size);
}