package com.quiz.ai.quizModule.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.quizModule.entity.subject.Course;

public record CourseResponse(
                @JsonProperty("id") String id,
                @JsonProperty("title") String title,
                @JsonProperty("image") String image,
                @JsonProperty("order_num") Integer orderNum,
                @JsonProperty("active") Boolean active,
                @JsonProperty("semester") String semester,
                @JsonProperty("domain_id") String domainId,
                @JsonProperty("level_id") String levelId,
                @JsonProperty("subject_id") String subjectId,
                @JsonProperty("domain") DomainSummaryResponse domain) {

        public static CourseResponse fromEntity(Course course) {
                return new CourseResponse(
                                course.getId(),
                                course.getTitle(),
                                course.getImage(),
                                course.getOrderNum(),
                                course.getActive(),
                                course.getSemester() == null ? null : course.getSemester().name(),
                                course.getDomain() == null ? null : course.getDomain().getId(),
                                course.getLevel() == null ? null : course.getLevel().getId(),
                                course.getSubject() == null ? null : course.getSubject().getId(),
                                course.getDomain() == null ? null
                                                : new DomainSummaryResponse(
                                                                course.getDomain().getId(),
                                                                course.getDomain().getTitle()));
        }
}