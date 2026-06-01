package com.quiz.ai.interfaces.rest;

import com.quiz.ai.domains.quiz.Quiz;
import com.quiz.ai.domains.school.Level;
import com.quiz.ai.domains.subject.Course;
import com.quiz.ai.domains.subject.Domain;
import com.quiz.ai.domains.subject.Subject;
import com.quiz.ai.enums.Cycle;
import com.quiz.ai.enums.Semester;
import com.quiz.ai.enums.Type;
import com.quiz.ai.application.dto.quiz.QuizzesByTypeResponse;
import com.quiz.ai.application.service.QuizService;
import com.quiz.ai.repository.CourseRepository;
import com.quiz.ai.repository.DomainRepository;
import com.quiz.ai.repository.LevelRepository;
import com.quiz.ai.repository.QuizRepository;
import com.quiz.ai.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class QuizControllerIntegrationTest {

    @Autowired
    private QuizService quizService;

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

    @Test
    void shouldReturnQuizzesGroupedByType() {
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

        Quiz flashcard1 = new Quiz();
        flashcard1.setTitle("Square Formula");
        flashcard1.setType(Type.FLASHCARD);
        flashcard1.setCourse(savedCourse);
        quizRepository.save(flashcard1);

        Quiz flashcard2 = new Quiz();
        flashcard2.setTitle("Circle Formula");
        flashcard2.setType(Type.FLASHCARD);
        flashcard2.setCourse(savedCourse);
        quizRepository.save(flashcard2);

        Quiz quiz1 = new Quiz();
        quiz1.setTitle("Algebra Quiz 1");
        quiz1.setType(Type.QUIZ);
        quiz1.setCourse(savedCourse);
        quizRepository.save(quiz1);

        QuizzesByTypeResponse response = quizService.getQuizzesByCourseId(savedCourse.getId());
        assertNotNull(response);
        assertEquals(2, response.flashcards().size());
        assertEquals(1, response.quizzes().size());
        assertEquals("Circle Formula", response.flashcards().get(0).title());
        assertEquals("Square Formula", response.flashcards().get(1).title());
        assertEquals("Algebra Quiz 1", response.quizzes().get(0).title());
    }
}
