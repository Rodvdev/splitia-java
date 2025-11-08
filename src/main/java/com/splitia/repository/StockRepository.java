package com.splitia.repository;

import com.splitia.model.inventory.Stock;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {
    
    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT s FROM Stock s WHERE s.product.id = :productId AND s.deletedAt IS NULL")
    Optional<Stock> findByProductId(@Param("productId") UUID productId);
    
    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT s FROM Stock s WHERE s.quantity <= s.minQuantity AND s.deletedAt IS NULL")
    List<Stock> findLowStock();
    
    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT s FROM Stock s WHERE s.location = :location AND s.deletedAt IS NULL")
    List<Stock> findByLocation(@Param("location") String location);
}

