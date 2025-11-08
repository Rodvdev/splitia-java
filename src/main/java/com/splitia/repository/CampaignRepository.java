package com.splitia.repository;

import com.splitia.model.enums.CampaignStatus;
import com.splitia.model.enums.CampaignType;
import com.splitia.model.marketing.Campaign;
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
public interface CampaignRepository extends JpaRepository<Campaign, UUID>, JpaSpecificationExecutor<Campaign> {
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT c FROM Campaign c WHERE c.deletedAt IS NULL")
    Page<Campaign> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT c FROM Campaign c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Campaign> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT c FROM Campaign c WHERE c.status = :status AND c.deletedAt IS NULL")
    Page<Campaign> findByStatus(@Param("status") CampaignStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT c FROM Campaign c WHERE c.type = :type AND c.deletedAt IS NULL")
    Page<Campaign> findByType(@Param("type") CampaignType type, Pageable pageable);
}

