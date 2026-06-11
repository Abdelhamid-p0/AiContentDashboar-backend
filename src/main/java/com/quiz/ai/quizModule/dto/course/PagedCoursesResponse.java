package com.quiz.ai.quizModule.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PagedCoursesResponse(
                @JsonProperty("courses") List<CourseResponse> courses,
                @JsonProperty("page") int page,
                @JsonProperty("size") int size,
                @JsonProperty("total_elements") long totalElements,
                @JsonProperty("total_pages") int totalPages) {
}
