package com.splitia.repository;

import com.splitia.model.procurement.VendorContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendorContractRepository extends JpaRepository<VendorContract, UUID> {
    @EntityGraph(attributePaths = {"vendor", "createdBy"})
    @Query("SELECT vc FROM VendorContract vc WHERE vc.deletedAt IS NULL")
    Page<VendorContract> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"vendor", "createdBy"})
    @Query("SELECT vc FROM VendorContract vc WHERE vc.id = :id AND vc.deletedAt IS NULL")
    Optional<VendorContract> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"vendor", "createdBy"})
    @Query("SELECT vc FROM VendorContract vc WHERE vc.vendor.id = :vendorId AND vc.deletedAt IS NULL")
    List<VendorContract> findByVendorId(@Param("vendorId") UUID vendorId);
    
    @EntityGraph(attributePaths = {"vendor", "createdBy"})
    @Query("SELECT vc FROM VendorContract vc WHERE vc.endDate >= :date AND vc.deletedAt IS NULL")
    List<VendorContract> findActiveContracts(@Param("date") LocalDate date);
}

