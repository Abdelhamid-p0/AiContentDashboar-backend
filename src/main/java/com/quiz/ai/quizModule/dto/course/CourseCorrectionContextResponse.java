package com.quiz.ai.quizModule.dto.course;

import com.quiz.ai.quizModule.entity.subject.Course;

public record CourseCorrectionContextResponse(
        String level,
        String subject,
        String domain,
        String semester) {
    public static CourseCorrectionContextResponse fromEntity(Course course) {
        return new CourseCorrectionContextResponse(
                course.getLevel() == null ? null : course.getLevel().getLevelName(),
                course.getSubject() == null ? null : course.getSubject().getTitle(),
                course.getDomain() == null ? null : course.getDomain().getTitle(),
                course.getSemester() == null ? null : course.getSemester().name());
    }
}
