package com.splitia.repository;

import com.splitia.model.ExpenseShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, UUID> {
    @Query("SELECT es FROM ExpenseShare es WHERE es.expense.id = :expenseId AND es.deletedAt IS NULL")
    List<ExpenseShare> findByExpenseId(@Param("expenseId") UUID expenseId);
    
    @Query("SELECT es FROM ExpenseShare es WHERE es.user.id = :userId AND es.deletedAt IS NULL")
    List<ExpenseShare> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT es FROM ExpenseShare es WHERE es.user.id = :userId AND es.deletedAt IS NULL")
    Page<ExpenseShare> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT es FROM ExpenseShare es WHERE es.id = :id AND es.deletedAt IS NULL")
    Optional<ExpenseShare> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

