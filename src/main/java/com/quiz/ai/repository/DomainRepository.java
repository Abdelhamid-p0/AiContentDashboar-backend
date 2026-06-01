package com.quiz.ai.repository;

import com.quiz.ai.domains.subject.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository extends JpaRepository<Domain, String> {
}
