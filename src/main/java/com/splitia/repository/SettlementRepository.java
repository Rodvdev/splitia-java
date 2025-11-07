package com.splitia.repository;

import com.splitia.model.Settlement;
import com.splitia.model.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    List<Settlement> findByGroupId(UUID groupId);
    List<Settlement> findByGroupIdAndStatus(UUID groupId, SettlementStatus status);
    List<Settlement> findByInitiatedByIdOrSettledWithUserId(UUID initiatedById, UUID settledWithUserId);
}

