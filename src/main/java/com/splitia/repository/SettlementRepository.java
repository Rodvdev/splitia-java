package com.splitia.repository;

import com.splitia.model.Settlement;
import com.splitia.model.enums.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    @Query("SELECT s FROM Settlement s WHERE s.group.id = :groupId AND s.deletedAt IS NULL")
    List<Settlement> findByGroupId(@Param("groupId") UUID groupId);
    
    @Query("SELECT s FROM Settlement s WHERE s.group.id = :groupId AND s.deletedAt IS NULL")
    Page<Settlement> findByGroupId(@Param("groupId") UUID groupId, Pageable pageable);
    
    @Query("SELECT s FROM Settlement s WHERE s.group.id = :groupId AND s.status = :status AND s.deletedAt IS NULL")
    List<Settlement> findByGroupIdAndStatus(@Param("groupId") UUID groupId, @Param("status") SettlementStatus status);
    
    @Query("SELECT s FROM Settlement s WHERE (s.initiatedBy.id = :initiatedById OR s.settledWithUser.id = :settledWithUserId) AND s.deletedAt IS NULL")
    List<Settlement> findByInitiatedByIdOrSettledWithUserId(@Param("initiatedById") UUID initiatedById, @Param("settledWithUserId") UUID settledWithUserId);
    
    @Query("SELECT s FROM Settlement s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Settlement> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

