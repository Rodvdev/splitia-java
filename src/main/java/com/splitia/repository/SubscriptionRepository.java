package com.splitia.repository;

import com.splitia.model.Subscription;
import com.splitia.model.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByUserIdAndStatus(UUID userId, SubscriptionStatus status);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
    List<Subscription> findByUserId(UUID userId);
}

