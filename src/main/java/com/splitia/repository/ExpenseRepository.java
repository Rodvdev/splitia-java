package com.splitia.repository;

import com.splitia.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    @Query("SELECT e FROM Expense e WHERE e.paidBy.id = :paidById AND e.deletedAt IS NULL")
    Page<Expense> findByPaidById(@Param("paidById") UUID paidById, Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId AND e.deletedAt IS NULL")
    Page<Expense> findByGroupId(@Param("groupId") UUID groupId, Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId AND e.deletedAt IS NULL")
    List<Expense> findByGroupId(@Param("groupId") UUID groupId);
    
    @Query("SELECT e FROM Expense e WHERE e.category.id = :categoryId AND e.deletedAt IS NULL")
    Page<Expense> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId AND e.date BETWEEN :startDate AND :endDate AND e.deletedAt IS NULL")
    Page<Expense> findByGroupIdAndDateBetween(
        @Param("groupId") UUID groupId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    @Query("SELECT e FROM Expense e WHERE (e.paidBy.id = :userId OR EXISTS (SELECT es FROM ExpenseShare es WHERE es.expense.id = e.id AND es.user.id = :userId AND es.deletedAt IS NULL)) AND e.deletedAt IS NULL")
    Page<Expense> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Expense> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

