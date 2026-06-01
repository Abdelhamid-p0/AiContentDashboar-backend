package com.quiz.ai.repository;

import com.quiz.ai.domains.subject.Course;
import com.quiz.ai.enums.Semester;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("""
            select distinct course
            from Course course
            left join fetch course.domain
            left join fetch course.level
            left join fetch course.subject
            where (:levelId is null or course.level.id = :levelId)
              and (:subjectId is null or course.subject.id = :subjectId)
              and (:semester is null or course.semester = :semester)
            order by course.orderNum asc, course.title asc
            """)
    List<Course> findAllFiltered(
            @Param("levelId") String levelId,
            @Param("subjectId") String subjectId,
            @Param("semester") Semester semester);

    @Query(value = """
            select distinct course
            from Course course
            left join course.domain
            left join course.level
            left join course.subject
            where (:levelId is null or course.level.id = :levelId)
              and (:subjectId is null or course.subject.id = :subjectId)
              and (:semester is null or course.semester = :semester)
            order by course.orderNum asc, course.title asc
            """, countQuery = """
            select count(distinct course)
            from Course course
            left join course.domain
            left join course.level
            left join course.subject
            where (:levelId is null or course.level.id = :levelId)
              and (:subjectId is null or course.subject.id = :subjectId)
              and (:semester is null or course.semester = :semester)
            """)
    Page<Course> findAllFiltered(
            @Param("levelId") String levelId,
            @Param("subjectId") String subjectId,
            @Param("semester") Semester semester,
            Pageable pageable);
}
