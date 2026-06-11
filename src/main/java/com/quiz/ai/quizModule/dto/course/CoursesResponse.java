package com.quiz.ai.quizModule.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CoursesResponse(
                @JsonProperty("courses") List<CourseResponse> courses) {
}