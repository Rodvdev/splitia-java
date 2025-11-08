package com.splitia.repository;

import com.splitia.model.finance.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    @EntityGraph(attributePaths = {"transactions", "transactions.account"})
    @Query("SELECT je FROM JournalEntry je WHERE je.deletedAt IS NULL")
    Page<JournalEntry> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"transactions", "transactions.account"})
    @Query("SELECT je FROM JournalEntry je WHERE je.id = :id AND je.deletedAt IS NULL")
    Optional<JournalEntry> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"transactions", "transactions.account"})
    @Query("SELECT je FROM JournalEntry je WHERE je.entryNumber = :entryNumber AND je.deletedAt IS NULL")
    Optional<JournalEntry> findByEntryNumber(@Param("entryNumber") String entryNumber);
    
    @EntityGraph(attributePaths = {"transactions", "transactions.account"})
    @Query("SELECT je FROM JournalEntry je WHERE je.date BETWEEN :startDate AND :endDate AND je.deletedAt IS NULL")
    Page<JournalEntry> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
}

