package com.quiz.ai.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.quiz.ai.domains.question.Answer;
import com.quiz.ai.domains.question.Objective;
import com.quiz.ai.domains.question.Question;
import com.quiz.ai.domains.question.SubQuestion;
import com.quiz.ai.domains.quiz.Quiz;
import com.quiz.ai.domains.school.Level;
import com.quiz.ai.domains.subject.Course;
import com.quiz.ai.domains.subject.Domain;
import com.quiz.ai.domains.subject.Subject;
import com.quiz.ai.enums.QuestionType;
import com.quiz.ai.enums.Type;
import com.quiz.ai.enums.Cycle;
import com.quiz.ai.enums.Semester;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectiveRepository objectiveRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private SubQuestionRepository subQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void testCreateAndRetrieveSampleData() {
        // Create Level
        Level level = new Level();
        level.setLevelName("Middle School");
        level.setCycle(Cycle.MIDDLE_SCHOOL);
        Level savedLevel = levelRepository.save(level);
        assertNotNull(savedLevel.getId());
        System.out.println("✓ Level created: " + savedLevel.getId());

        // Create Subject
        Subject subject = new Subject();
        subject.setTitle("Mathematics");
        Subject savedSubject = subjectRepository.save(subject);
        assertNotNull(savedSubject.getId());
        System.out.println("✓ Subject created: " + savedSubject.getId());

        // Create Domain
        Domain domain = new Domain();
        domain.setTitle("Algebra");
        Domain savedDomain = domainRepository.save(domain);
        assertNotNull(savedDomain.getId());
        System.out.println("✓ Domain created: " + savedDomain.getId());

        // Create Course
        Course course = new Course();
        course.setTitle("Algebra Basics");
        course.setLevel(savedLevel);
        course.setSubject(savedSubject);
        course.setDomain(savedDomain);
        course.setSemester(Semester.S1);
        course.setOrderNum(1);
        course.setActive(true);
        Course savedCourse = courseRepository.save(course);
        assertNotNull(savedCourse.getId());
        System.out.println("✓ Course created: " + savedCourse.getId());

        // Create Objective
        Objective objective = new Objective();
        objective.setObjective("Learn basic algebra concepts");
        Objective savedObjective = objectiveRepository.save(objective);
        assertNotNull(savedObjective.getId());
        System.out.println("✓ Objective created: " + savedObjective.getId());

        // Create Quiz
        Quiz quiz = new Quiz();
        quiz.setTitle("Algebra Quiz 1");
        quiz.setType(Type.QUIZ);
        quiz.setCourse(savedCourse);
        Quiz savedQuiz = quizRepository.save(quiz);
        assertNotNull(savedQuiz.getId());
        System.out.println("✓ Quiz created: " + savedQuiz.getId());

        // Create Answers (don't save separately - let cascade handle it)
        Answer correctAnswer = new Answer();
        correctAnswer.setAnswer("5");
        correctAnswer.setOrderNum(1);
        correctAnswer.setRight(true);

        Answer wrongAnswer = new Answer();
        wrongAnswer.setAnswer("3");
        wrongAnswer.setOrderNum(2);
        wrongAnswer.setRight(false);
        System.out.println("✓ Answers created");

        // Create SubQuestion with Answers (don't save separately - let cascade handle
        // it)
        SubQuestion subQuestion = new SubQuestion();
        subQuestion.setQuestion("What is 2 + 3?");
        subQuestion.setOrderNum(1);
        subQuestion.setAnswers(List.of(correctAnswer, wrongAnswer));
        System.out.println("✓ SubQuestion created (not yet persisted)");

        // Create Question with SubQuestions and save everything
        Question question = new Question();
        question.setQuestion("Solve the equation");
        question.setQuestionType(QuestionType.MULTI_CHOICE);
        question.setOrderNum(1);
        question.setObjective(savedObjective);
        question.setSubQuestions(List.of(subQuestion)); // Add unsaved subQuestion
        question.setQuiz(savedQuiz);
        Question savedQuestion = questionRepository.save(question);
        assertNotNull(savedQuestion.getId());
        System.out.println("✓ Question created: " + savedQuestion.getId());

        // Verify relationships load correctly
        Question retrievedQuestion = questionRepository.findById(savedQuestion.getId()).orElse(null);
        assertNotNull(retrievedQuestion);
        assertNotNull(retrievedQuestion.getQuiz());
        assertNotNull(retrievedQuestion.getQuiz().getCourse());
        assertNotNull(retrievedQuestion.getQuiz().getCourse().getLevel());
        assertNotNull(retrievedQuestion.getQuiz().getCourse().getSubject());
        assertNotNull(retrievedQuestion.getQuiz().getCourse().getDomain());
        assertNotNull(retrievedQuestion.getObjective());
        assertFalse(retrievedQuestion.getSubQuestions().isEmpty());

        System.out.println("\n✓✓✓ All relationships verified successfully! ✓✓✓");
        System.out.println("Quiz: " + retrievedQuestion.getQuiz().getTitle());
        System.out.println("Course: " + retrievedQuestion.getQuiz().getCourse().getTitle());
        System.out.println("Level: " + retrievedQuestion.getQuiz().getCourse().getLevel().getLevelName());
        System.out.println("Subject: " + retrievedQuestion.getQuiz().getCourse().getSubject().getTitle());
        System.out.println("Domain: " + retrievedQuestion.getQuiz().getCourse().getDomain().getTitle());
        System.out.println("Objective: " + retrievedQuestion.getObjective().getObjective());
    }

    @Test
    void testRepositoriesAreFunctional() {
        // Just verify all repositories are accessible
        assertNotNull(levelRepository);
        assertNotNull(subjectRepository);
        assertNotNull(domainRepository);
        assertNotNull(courseRepository);
        assertNotNull(objectiveRepository);
        assertNotNull(quizRepository);
        assertNotNull(answerRepository);
        assertNotNull(subQuestionRepository);
        assertNotNull(questionRepository);
        System.out.println("✓ All 9 repositories are functional");
    }
}
