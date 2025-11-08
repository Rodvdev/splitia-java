package com.splitia.repository;

import com.splitia.model.marketing.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT et FROM EmailTemplate et WHERE et.deletedAt IS NULL")
    Page<EmailTemplate> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT et FROM EmailTemplate et WHERE et.id = :id AND et.deletedAt IS NULL")
    Optional<EmailTemplate> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT et FROM EmailTemplate et WHERE et.name = :name AND et.deletedAt IS NULL")
    Optional<EmailTemplate> findByName(@Param("name") String name);
}

