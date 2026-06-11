package com.quiz.ai.interfaces.rest;

import com.quiz.ai.quiz_module.dto.course.PagedCoursesResponse;
import com.quiz.ai.quiz_module.entity.school.Level;
import com.quiz.ai.quiz_module.entity.subject.Course;
import com.quiz.ai.quiz_module.entity.subject.Domain;
import com.quiz.ai.quiz_module.entity.subject.Subject;
import com.quiz.ai.quiz_module.enums.Cycle;
import com.quiz.ai.quiz_module.enums.Semester;
import com.quiz.ai.quiz_module.repository.CourseRepository;
import com.quiz.ai.quiz_module.repository.DomainRepository;
import com.quiz.ai.quiz_module.repository.LevelRepository;
import com.quiz.ai.quiz_module.repository.SubjectRepository;
import com.quiz.ai.quiz_module.service.interfaces.CourseService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class CourseControllerIntegrationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldReturnFilteredCoursesWithDomainSummary() throws Exception {
        Level level = new Level();
        level.setLevelName("Middle School");
        level.setCycle(Cycle.MIDDLE_SCHOOL);
        Level savedLevel = levelRepository.save(level);

        Subject subject = new Subject();
        subject.setTitle("Mathematics");
        Subject savedSubject = subjectRepository.save(subject);

        Domain geometry = new Domain();
        geometry.setTitle("Géométrie");
        Domain savedGeometry = domainRepository.save(geometry);

        Domain algebra = new Domain();
        algebra.setTitle("Nombres et calculs");
        Domain savedAlgebra = domainRepository.save(algebra);

        Course firstCourse = new Course();
        firstCourse.setTitle("Les figures géométrique usuelles");
        firstCourse.setImage(null);
        firstCourse.setOrderNum(3);
        firstCourse.setActive(null);
        firstCourse.setSemester(Semester.S1);
        firstCourse.setDomain(savedGeometry);
        firstCourse.setLevel(savedLevel);
        firstCourse.setSubject(savedSubject);
        courseRepository.save(firstCourse);

        Course secondCourse = new Course();
        secondCourse.setTitle("Les nombres de 0 à 99");
        secondCourse.setImage(null);
        secondCourse.setOrderNum(1);
        secondCourse.setActive(true);
        secondCourse.setSemester(Semester.S1);
        secondCourse.setDomain(savedAlgebra);
        secondCourse.setLevel(savedLevel);
        secondCourse.setSubject(savedSubject);
        courseRepository.save(secondCourse);

        PagedCoursesResponse response = courseService.getCourses(savedLevel.getId(), savedSubject.getId(), Semester.S1,
                0, 20);
        assertNotNull(response);
        assertNotNull(response.courses());
        assertEquals(2, response.courses().size());
        assertEquals("Les nombres de 0 à 99", response.courses().get(0).title());
        assertEquals(1, response.courses().get(0).orderNum());
        assertEquals("S1", response.courses().get(0).semester());
        assertEquals(savedLevel.getId(), response.courses().get(0).levelId());
        assertEquals(savedSubject.getId(), response.courses().get(0).subjectId());
        assertEquals(savedAlgebra.getId(), response.courses().get(0).domain().id());
        assertEquals("Nombres et calculs", response.courses().get(0).domain().title());
        assertEquals("Les figures géométrique usuelles", response.courses().get(1).title());
        assertEquals(savedGeometry.getId(), response.courses().get(1).domain().id());
        assertEquals("Géométrie", response.courses().get(1).domain().title());
    }
}