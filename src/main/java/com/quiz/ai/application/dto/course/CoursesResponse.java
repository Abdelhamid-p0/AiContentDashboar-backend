package com.quiz.ai.application.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CoursesResponse(
        @JsonProperty("courses") List<CourseResponse> courses) {
}