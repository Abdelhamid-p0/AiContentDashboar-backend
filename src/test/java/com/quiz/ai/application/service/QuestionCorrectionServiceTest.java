package com.quiz.ai.application.service;

import com.quiz.ai.prompt_builder_module.config.llm.client.LLMClient;
import com.quiz.ai.prompt_builder_module.config.llm.prompt.PromptBuilder;
import com.quiz.ai.prompt_builder_module.service.QuestionCorrectionService;
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
import com.quiz.ai.quiz_module.repository.QuestionRepository;
import com.quiz.ai.quiz_module.repository.QuizRepository;
import com.quiz.ai.quiz_module.repository.SubjectRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class QuestionCorrectionServiceTest {

    @Autowired
    private QuestionCorrectionService questionCorrectionService;

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

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private LLMClient llmClient;

    @Autowired
    private PromptBuilder promptBuilder;

    @Test
    void testQuestionCorrectionService() {
        // Create test data
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
        objective.setObjective("Understand linear equations");
        Objective savedObjective = objectiveRepository.save(objective);

        Answer answer1 = Answer.builder()
                .answer("x = 2")
                .image("image1")
                .isRight(true)
                .answerAudio("audio1")
                .orderNum(1)
                .build();

        Answer answer2 = Answer.builder()
                .answer("x = 5")
                .image("image2")
                .isRight(false)
                .answerAudio("audio2")
                .orderNum(2)
                .build();

        SubQuestion subQuestion = new SubQuestion();
        subQuestion.setQuestion("Solve: 2x - 2 = 2");
        subQuestion.setOrderNum(1);
        subQuestion.setAnswers(List.of(answer1, answer2));

        Question question = new Question();
        question.setQuestion("What is the solution to 2x - 2 = 2?");
        question.setQuestionType(QuestionType.ONE_CHOICE);
        question.setImage("question_image");
        question.setFeedback("Good job!");
        question.setQuestionAudio("question_audio");
        question.setOrderNum(1);
        question.setObjective(savedObjective);
        question.setQuiz(quiz);
        question.setSubQuestions(List.of(subQuestion));

        quiz.setQuestions(List.of(question));
        Quiz savedQuiz = quizRepository.save(quiz);

        // Test the service (commented out because it requires real API key)
        log.info("Question correction service test setup completed");

        // This would call the actual LLM - skip in tests without valid API key
        // QuestionCorrectionResponse response =
        // questionCorrectionService.correctQuestion(question.getId());
        // assertNotNull(response);
    }

    @Test
    void testPromptBuilderGeneratesValidPrompt() {
        Level level = new Level();
        level.setLevelName("High School");
        level.setCycle(Cycle.HIGH_SCHOOL);
        Level savedLevel = levelRepository.save(level);

        Subject subject = new Subject();
        subject.setTitle("Physics");
        Subject savedSubject = subjectRepository.save(subject);

        Domain domain = new Domain();
        domain.setTitle("Mechanics");
        Domain savedDomain = domainRepository.save(domain);

        Course course = new Course();
        course.setTitle("Classical Mechanics");
        course.setLevel(savedLevel);
        course.setSubject(savedSubject);
        course.setDomain(savedDomain);
        course.setSemester(Semester.S2);
        course.setOrderNum(1);
        course.setActive(true);
        Course savedCourse = courseRepository.save(course);

        String systemMessage = promptBuilder.buildSystemMessage(savedCourse);
        assertNotNull(systemMessage);
        log.info("System message: {}", systemMessage);
    }
}
