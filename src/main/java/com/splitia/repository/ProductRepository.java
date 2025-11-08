package com.splitia.repository;

import com.splitia.model.enums.ProductType;
import com.splitia.model.inventory.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    
    @EntityGraph(attributePaths = {"stock", "variants"})
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    Page<Product> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"stock", "variants"})
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"stock", "variants"})
    @Query("SELECT p FROM Product p WHERE p.sku = :sku AND p.deletedAt IS NULL")
    Optional<Product> findBySku(@Param("sku") String sku);
    
    @EntityGraph(attributePaths = {"stock", "variants"})
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.deletedAt IS NULL")
    Page<Product> findByCategory(@Param("category") String category, Pageable pageable);
    
    @EntityGraph(attributePaths = {"stock", "variants"})
    @Query("SELECT p FROM Product p WHERE p.type = :type AND p.deletedAt IS NULL")
    Page<Product> findByType(@Param("type") ProductType type, Pageable pageable);
}

