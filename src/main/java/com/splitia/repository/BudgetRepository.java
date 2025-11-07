package com.splitia.repository;

import com.splitia.model.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.month = :month AND b.year = :year AND b.category.id = :categoryId AND b.deletedAt IS NULL")
    Optional<Budget> findByUserIdAndMonthAndYearAndCategoryId(
        @Param("userId") UUID userId, 
        @Param("month") Integer month, 
        @Param("year") Integer year, 
        @Param("categoryId") UUID categoryId
    );
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.month = :month AND b.year = :year AND b.deletedAt IS NULL")
    Optional<Budget> findByUserIdAndMonthAndYear(@Param("userId") UUID userId, @Param("month") Integer month, @Param("year") Integer year);
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.deletedAt IS NULL")
    Page<Budget> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT b FROM Budget b WHERE b.id = :id AND b.deletedAt IS NULL")
    Optional<Budget> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

