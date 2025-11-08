package com.splitia.repository;

import com.splitia.model.contact.ContactTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactTagRepository extends JpaRepository<ContactTag, UUID> {
    @Query("SELECT t FROM ContactTag t WHERE t.name = :name AND t.deletedAt IS NULL")
    Optional<ContactTag> findByName(@Param("name") String name);
}

