package com.quiz.ai.quizModule.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quizModule.entity.quiz.Quiz;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {

    @Query("""
            select quiz
            from Quiz quiz
            where quiz.course.id = :courseId
            order by quiz.title asc
            """)
    List<Quiz> findByCourseId(@Param("courseId") String courseId);
}
