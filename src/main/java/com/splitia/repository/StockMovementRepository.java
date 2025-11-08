package com.splitia.repository;

import com.splitia.model.enums.StockMovementType;
import com.splitia.model.inventory.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    
    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId AND sm.deletedAt IS NULL ORDER BY sm.date DESC")
    Page<StockMovement> findByProductId(@Param("productId") UUID productId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT sm FROM StockMovement sm WHERE sm.date BETWEEN :startDate AND :endDate AND sm.deletedAt IS NULL ORDER BY sm.date DESC")
    Page<StockMovement> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
    
    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT sm FROM StockMovement sm WHERE sm.type = :type AND sm.deletedAt IS NULL ORDER BY sm.date DESC")
    Page<StockMovement> findByType(@Param("type") StockMovementType type, Pageable pageable);
    
    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId AND sm.type = :type AND sm.deletedAt IS NULL ORDER BY sm.date DESC")
    List<StockMovement> findByProductIdAndType(@Param("productId") UUID productId, @Param("type") StockMovementType type);
}

