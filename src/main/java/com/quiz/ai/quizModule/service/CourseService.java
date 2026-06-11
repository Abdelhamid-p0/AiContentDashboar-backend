package com.quiz.ai.quizModule.service;

import com.quiz.ai.quizModule.dto.course.PagedCoursesResponse;
import com.quiz.ai.quizModule.enums.Semester;

public interface CourseService {
    PagedCoursesResponse getCourses(String levelId, String subjectId, Semester semester, int page, int size);
}