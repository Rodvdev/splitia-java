package com.splitia.repository;

import com.splitia.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    
    @Query("SELECT p FROM Plan p WHERE p.name = :name AND p.deletedAt IS NULL")
    Optional<Plan> findByName(@Param("name") String name);
    
    @Query("SELECT p FROM Plan p WHERE p.isActive = true AND p.deletedAt IS NULL")
    java.util.List<Plan> findAllActive();
    
    @Query("SELECT p FROM Plan p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Plan> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

