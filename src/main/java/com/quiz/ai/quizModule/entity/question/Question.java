package com.quiz.ai.quizModule.entity.question;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import com.quiz.ai.quizModule.entity.quiz.Quiz;
import com.quiz.ai.quizModule.enums.QuestionType;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    @Column(columnDefinition = "text")
    private String question;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    private String image;

    @Column(columnDefinition = "text")
    private String feedback;

    private String feedbackAudio;

    private String questionAudio;

    private Integer orderNum;

    @ManyToOne
    private Objective objective;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "question_id")
    private List<SubQuestion> subQuestions;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
