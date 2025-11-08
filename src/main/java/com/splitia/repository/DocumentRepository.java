package com.splitia.repository;

import com.splitia.model.common.Document;
import com.splitia.model.enums.DocumentType;
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
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    @EntityGraph(attributePaths = {"uploadedBy"})
    @Query("SELECT d FROM Document d WHERE d.deletedAt IS NULL")
    Page<Document> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"uploadedBy"})
    @Query("SELECT d FROM Document d WHERE d.id = :id AND d.deletedAt IS NULL")
    Optional<Document> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"uploadedBy"})
    @Query("SELECT d FROM Document d WHERE d.entityType = :entityType AND d.entityId = :entityId AND d.deletedAt IS NULL")
    Page<Document> findByEntity(@Param("entityType") String entityType, @Param("entityId") UUID entityId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"uploadedBy"})
    @Query("SELECT d FROM Document d WHERE d.type = :type AND d.deletedAt IS NULL")
    Page<Document> findByType(@Param("type") DocumentType type, Pageable pageable);
}

