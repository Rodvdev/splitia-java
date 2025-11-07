package com.splitia.repository;

import com.splitia.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    Optional<Budget> findByUserIdAndMonthAndYearAndCategoryId(
        UUID userId, Integer month, Integer year, UUID categoryId
    );
    
    Optional<Budget> findByUserIdAndMonthAndYear(UUID userId, Integer month, Integer year);
}

