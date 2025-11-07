package com.splitia.repository;

import com.splitia.model.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, UUID> {
    List<ExpenseShare> findByExpenseId(UUID expenseId);
    List<ExpenseShare> findByUserId(UUID userId);
    void deleteByExpenseId(UUID expenseId);
}

