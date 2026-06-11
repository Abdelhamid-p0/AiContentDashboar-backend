package com.quiz.ai.quizModule.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CourseFiltersResponse(
                @JsonProperty("levels") List<FilterOptionResponse> levels,
                @JsonProperty("subjects") List<FilterOptionResponse> subjects,
                @JsonProperty("semesters") List<FilterOptionResponse> semesters) {
}
