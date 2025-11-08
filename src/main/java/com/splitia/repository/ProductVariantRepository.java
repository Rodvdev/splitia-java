package com.splitia.repository;

import com.splitia.model.inventory.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.deletedAt IS NULL")
    List<ProductVariant> findByProductId(@Param("productId") UUID productId);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.sku = :sku AND pv.deletedAt IS NULL")
    Optional<ProductVariant> findBySku(@Param("sku") String sku);
}

