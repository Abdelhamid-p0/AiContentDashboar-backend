package com.quiz.ai.quizModule.entity.subject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.quiz.ai.quizModule.entity.quiz.Quiz;
import com.quiz.ai.quizModule.entity.school.Level;
import com.quiz.ai.quizModule.enums.Semester;

import jakarta.persistence.CascadeType;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private String title;

    private String image;

    private Integer orderNum;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    @JsonBackReference
    private Domain domain;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @JsonBackReference
    private Subject subject;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonBackReference
    private List<Quiz> quizzes;

    protected Boolean active = true;

    public void updateStatus(final Boolean status) {
        this.active = status;
    }
}
