package com.splitia.repository;

import com.splitia.model.enums.PurchaseOrderStatus;
import com.splitia.model.procurement.PurchaseOrder;
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
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID>, JpaSpecificationExecutor<PurchaseOrder> {
    @EntityGraph(attributePaths = {"vendor", "createdBy", "items", "items.product"})
    @Query("SELECT po FROM PurchaseOrder po WHERE po.deletedAt IS NULL")
    Page<PurchaseOrder> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"vendor", "createdBy", "items", "items.product"})
    @Query("SELECT po FROM PurchaseOrder po WHERE po.id = :id AND po.deletedAt IS NULL")
    Optional<PurchaseOrder> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"vendor", "createdBy", "items", "items.product"})
    @Query("SELECT po FROM PurchaseOrder po WHERE po.orderNumber = :orderNumber AND po.deletedAt IS NULL")
    Optional<PurchaseOrder> findByOrderNumber(@Param("orderNumber") String orderNumber);
    
    @EntityGraph(attributePaths = {"vendor", "createdBy", "items", "items.product"})
    @Query("SELECT po FROM PurchaseOrder po WHERE po.status = :status AND po.deletedAt IS NULL")
    Page<PurchaseOrder> findByStatus(@Param("status") PurchaseOrderStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"vendor", "createdBy", "items", "items.product"})
    @Query("SELECT po FROM PurchaseOrder po WHERE po.vendor.id = :vendorId AND po.deletedAt IS NULL")
    Page<PurchaseOrder> findByVendorId(@Param("vendorId") UUID vendorId, Pageable pageable);
}

