package com.splitia.repository;

import com.splitia.model.enums.InvoiceStatus;
import com.splitia.model.finance.Invoice;
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
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    @EntityGraph(attributePaths = {"contact", "company", "createdBy", "items", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.deletedAt IS NULL")
    Page<Invoice> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"contact", "company", "createdBy", "items", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.id = :id AND i.deletedAt IS NULL")
    Optional<Invoice> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"contact", "company", "createdBy", "items", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.invoiceNumber = :invoiceNumber AND i.deletedAt IS NULL")
    Optional<Invoice> findByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);
    
    @EntityGraph(attributePaths = {"contact", "company", "createdBy", "items", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.deletedAt IS NULL")
    Page<Invoice> findByStatus(@Param("status") InvoiceStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"contact", "company", "createdBy", "items", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.contact.id = :contactId AND i.deletedAt IS NULL")
    Page<Invoice> findByContactId(@Param("contactId") UUID contactId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"contact", "company", "createdBy", "items", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId AND i.deletedAt IS NULL")
    Page<Invoice> findByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :date AND i.status NOT IN ('PAID', 'CANCELLED') AND i.deletedAt IS NULL")
    Page<Invoice> findOverdueInvoices(@Param("date") LocalDate date, Pageable pageable);
}

