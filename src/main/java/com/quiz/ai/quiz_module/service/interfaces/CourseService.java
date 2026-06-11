package com.quiz.ai.quiz_module.service.interfaces;

import com.quiz.ai.quiz_module.dto.course.PagedCoursesResponse;
import com.quiz.ai.quiz_module.enums.Semester;

public interface CourseService {
    PagedCoursesResponse getCourses(String levelId, String subjectId, Semester semester, int page, int size);
}