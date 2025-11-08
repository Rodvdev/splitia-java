package com.splitia.repository;

import com.splitia.model.enums.TimeEntryStatus;
import com.splitia.model.project.TimeEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, UUID> {
    @EntityGraph(attributePaths = {"project", "task", "user"})
    @Query("SELECT te FROM TimeEntry te WHERE te.deletedAt IS NULL")
    Page<TimeEntry> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"project", "task", "user"})
    @Query("SELECT te FROM TimeEntry te WHERE te.id = :id AND te.deletedAt IS NULL")
    java.util.Optional<TimeEntry> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"project", "task", "user"})
    @Query("SELECT te FROM TimeEntry te WHERE te.project.id = :projectId AND te.deletedAt IS NULL")
    Page<TimeEntry> findByProjectId(@Param("projectId") UUID projectId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"project", "task", "user"})
    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.deletedAt IS NULL")
    Page<TimeEntry> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"project", "task", "user"})
    @Query("SELECT te FROM TimeEntry te WHERE te.date BETWEEN :startDate AND :endDate AND te.deletedAt IS NULL")
    Page<TimeEntry> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
}

