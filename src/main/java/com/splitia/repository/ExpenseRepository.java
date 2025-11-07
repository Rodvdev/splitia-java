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
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findByPaidById(UUID paidById, Pageable pageable);
    
    Page<Expense> findByGroupId(UUID groupId, Pageable pageable);
    
    Page<Expense> findByCategoryId(UUID categoryId, Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId AND e.date BETWEEN :startDate AND :endDate")
    Page<Expense> findByGroupIdAndDateBetween(
        @Param("groupId") UUID groupId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    @Query("SELECT e FROM Expense e WHERE e.paidBy.id = :userId OR EXISTS (SELECT es FROM ExpenseShare es WHERE es.expense.id = e.id AND es.user.id = :userId)")
    Page<Expense> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    List<Expense> findByGroupId(UUID groupId);
}

