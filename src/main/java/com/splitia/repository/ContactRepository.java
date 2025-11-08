package com.splitia.repository;

import com.splitia.model.contact.Contact;
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
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    @EntityGraph(attributePaths = {"owner", "company", "tags"})
    @Query("SELECT c FROM Contact c WHERE c.deletedAt IS NULL")
    Page<Contact> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"owner", "company", "tags"})
    @Query("SELECT c FROM Contact c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Contact> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"owner", "company", "tags"})
    @Query("SELECT c FROM Contact c WHERE c.email = :email AND c.deletedAt IS NULL")
    Optional<Contact> findByEmail(@Param("email") String email);
    
    @EntityGraph(attributePaths = {"owner", "company", "tags"})
    @Query("SELECT c FROM Contact c WHERE c.company.id = :companyId AND c.deletedAt IS NULL")
    Page<Contact> findByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);
}

