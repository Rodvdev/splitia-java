package com.splitia.repository;

import com.splitia.model.Subscription;
import com.splitia.model.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = :status AND s.deletedAt IS NULL")
    Optional<Subscription> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") SubscriptionStatus status);
    
    @Query("SELECT s FROM Subscription s WHERE s.stripeSubscriptionId = :stripeSubscriptionId AND s.deletedAt IS NULL")
    Optional<Subscription> findByStripeSubscriptionId(@Param("stripeSubscriptionId") String stripeSubscriptionId);
    
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.deletedAt IS NULL")
    List<Subscription> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.deletedAt IS NULL")
    Page<Subscription> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT s FROM Subscription s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Subscription> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT s FROM Subscription s WHERE s.deletedAt IS NULL")
    Page<Subscription> findAllWithUser(Pageable pageable);
}

