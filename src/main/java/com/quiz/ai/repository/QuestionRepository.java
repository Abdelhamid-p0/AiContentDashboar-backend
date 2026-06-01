package com.quiz.ai.repository;

import com.quiz.ai.domains.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    @Query("""
            select question
            from Question question
            left join fetch question.objective
            where question.quiz.id = :quizId
            order by question.orderNum asc
            """)
    List<Question> findSummariesByQuizId(@Param("quizId") String quizId);

    @Query("""
            select distinct question
            from Question question
            left join fetch question.objective
            where question.id = :questionId
            """)
    Optional<Question> findDetailedById(@Param("questionId") String questionId);
}
