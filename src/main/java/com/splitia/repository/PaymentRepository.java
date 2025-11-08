package com.splitia.repository;

import com.splitia.model.finance.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @EntityGraph(attributePaths = {"invoice"})
    @Query("SELECT p FROM Payment p WHERE p.deletedAt IS NULL")
    Page<Payment> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"invoice"})
    @Query("SELECT p FROM Payment p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Payment> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"invoice"})
    @Query("SELECT p FROM Payment p WHERE p.invoice.id = :invoiceId AND p.deletedAt IS NULL")
    Page<Payment> findByInvoiceId(@Param("invoiceId") UUID invoiceId, Pageable pageable);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.invoice.id = :invoiceId AND p.deletedAt IS NULL")
    java.math.BigDecimal getTotalPaidForInvoice(@Param("invoiceId") UUID invoiceId);
}

