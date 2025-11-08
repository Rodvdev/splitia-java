package com.splitia.repository;

import com.splitia.model.contact.Company;
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
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    @EntityGraph(attributePaths = {"owner"})
    @Query("SELECT c FROM Company c WHERE c.deletedAt IS NULL")
    Page<Company> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"owner"})
    @Query("SELECT c FROM Company c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Company> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"owner"})
    @Query("SELECT c FROM Company c WHERE c.name = :name AND c.deletedAt IS NULL")
    Optional<Company> findByName(@Param("name") String name);
}

