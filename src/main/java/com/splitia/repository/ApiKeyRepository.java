package com.splitia.repository;

import com.splitia.model.common.ApiKey;
import com.splitia.model.enums.ApiKeyStatus;
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
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT ak FROM ApiKey ak WHERE ak.deletedAt IS NULL")
    Page<ApiKey> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT ak FROM ApiKey ak WHERE ak.id = :id AND ak.deletedAt IS NULL")
    Optional<ApiKey> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT ak FROM ApiKey ak WHERE ak.keyHash = :keyHash AND ak.deletedAt IS NULL")
    Optional<ApiKey> findByKeyHash(@Param("keyHash") String keyHash);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT ak FROM ApiKey ak WHERE ak.status = :status AND ak.deletedAt IS NULL")
    Page<ApiKey> findByStatus(@Param("status") ApiKeyStatus status, Pageable pageable);
}

