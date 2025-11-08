package com.splitia.repository;

import com.splitia.model.procurement.Vendor;
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
public interface VendorRepository extends JpaRepository<Vendor, UUID>, JpaSpecificationExecutor<Vendor> {
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT v FROM Vendor v WHERE v.deletedAt IS NULL")
    Page<Vendor> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT v FROM Vendor v WHERE v.id = :id AND v.deletedAt IS NULL")
    Optional<Vendor> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT v FROM Vendor v WHERE v.name = :name AND v.deletedAt IS NULL")
    Optional<Vendor> findByName(@Param("name") String name);
}

