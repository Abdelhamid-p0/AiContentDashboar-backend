package com.quiz.ai.interfaces.rest;

import com.quiz.ai.quiz_module.dto.question.QuestionResponse;
import com.quiz.ai.quiz_module.dto.question.QuestionsResponse;
import com.quiz.ai.quiz_module.entity.question.Answer;
import com.quiz.ai.quiz_module.entity.question.Objective;
import com.quiz.ai.quiz_module.entity.question.Question;
import com.quiz.ai.quiz_module.entity.question.SubQuestion;
import com.quiz.ai.quiz_module.entity.quiz.Quiz;
import com.quiz.ai.quiz_module.entity.school.Level;
import com.quiz.ai.quiz_module.entity.subject.Course;
import com.quiz.ai.quiz_module.entity.subject.Domain;
import com.quiz.ai.quiz_module.entity.subject.Subject;
import com.quiz.ai.quiz_module.enums.Cycle;
import com.quiz.ai.quiz_module.enums.QuestionType;
import com.quiz.ai.quiz_module.enums.Semester;
import com.quiz.ai.quiz_module.enums.Type;
import com.quiz.ai.quiz_module.repository.CourseRepository;
import com.quiz.ai.quiz_module.repository.DomainRepository;
import com.quiz.ai.quiz_module.repository.LevelRepository;
import com.quiz.ai.quiz_module.repository.ObjectiveRepository;
import com.quiz.ai.quiz_module.repository.QuizRepository;
import com.quiz.ai.quiz_module.repository.SubjectRepository;
import com.quiz.ai.quiz_module.service.interfaces.QuestionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class QuestionControllerIntegrationTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ObjectiveRepository objectiveRepository;

    @Test
    void shouldReturnQuestionsWithDetailsForQuiz() {
        Level level = new Level();
        level.setLevelName("Middle School");
        level.setCycle(Cycle.MIDDLE_SCHOOL);
        Level savedLevel = levelRepository.save(level);

        Subject subject = new Subject();
        subject.setTitle("Mathematics");
        Subject savedSubject = subjectRepository.save(subject);

        Domain domain = new Domain();
        domain.setTitle("Algebra");
        Domain savedDomain = domainRepository.save(domain);

        Course course = new Course();
        course.setTitle("Algebra Basics");
        course.setLevel(savedLevel);
        course.setSubject(savedSubject);
        course.setDomain(savedDomain);
        course.setSemester(Semester.S1);
        course.setOrderNum(1);
        course.setActive(true);
        Course savedCourse = courseRepository.save(course);

        Quiz quiz = new Quiz();
        quiz.setTitle("Quiz 1");
        quiz.setType(Type.QUIZ);
        quiz.setCourse(savedCourse);

        Objective objective = new Objective();
        objective.setObjective("Understand square equations");
        Objective savedObjective = objectiveRepository.save(objective);

        Answer answer1 = Answer.builder()
                .answer("4")
                .image("answer_image")
                .isRight(true)
                .answerAudio("answer_audio")
                .orderNum(1)
                .build();

        Answer answer2 = Answer.builder()
                .answer("8")
                .image("answer_image_2")
                .isRight(false)
                .answerAudio("answer_audio_2")
                .orderNum(2)
                .build();

        SubQuestion subQuestion = new SubQuestion();
        subQuestion.setQuestion("Sub question 1");
        subQuestion.setOrderNum(1);
        subQuestion.setAnswers(List.of(answer1, answer2));

        Question question = new Question();
        question.setQuestion("What is x in x² = 16?");
        question.setQuestionType(QuestionType.ONE_CHOICE);
        question.setImage("image_url");
        question.setFeedback("Good job!");
        question.setFeedbackAudio("audio_url");
        question.setQuestionAudio("question_audio_url");
        question.setOrderNum(1);
        question.setObjective(savedObjective);
        question.setQuiz(quiz);
        question.setSubQuestions(List.of(subQuestion));

        quiz.setQuestions(List.of(question));
        Quiz savedQuiz = quizRepository.save(quiz);

        QuestionsResponse response = questionService.getQuestionsByQuizId(savedQuiz.getId());

        assertNotNull(response);
        assertEquals(1, response.questions().size());

        var questionResponse = response.questions().get(0);
        assertEquals("What is x in x² = 16?", questionResponse.question());
        assertEquals("ONE_CHOICE", questionResponse.questionType());
        assertEquals(1, questionResponse.orderNum());

        assertNotNull(questionResponse.objective());
        assertEquals("Understand square equations", questionResponse.objective().objective());

        QuestionResponse details = questionService.getQuestionById(questionResponse.id());
        assertNotNull(details);
        assertEquals("image_url", details.image());
        assertEquals("Good job!", details.feedback());
        assertEquals("audio_url", details.feedbackAudio());
        assertEquals("question_audio_url", details.questionAudio());

        assertEquals(1, details.subQuestions().size());
        var subQuestionResponse = details.subQuestions().get(0);
        assertEquals("Sub question 1", subQuestionResponse.question());
        assertEquals(2, subQuestionResponse.answers().size());

        assertEquals("4", subQuestionResponse.answers().get(0).answer());
        assertEquals(true, subQuestionResponse.answers().get(0).isRight());
        assertEquals("8", subQuestionResponse.answers().get(1).answer());
        assertEquals(false, subQuestionResponse.answers().get(1).isRight());
    }
}
